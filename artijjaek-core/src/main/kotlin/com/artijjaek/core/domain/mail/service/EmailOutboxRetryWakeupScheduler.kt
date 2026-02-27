package com.artijjaek.core.domain.mail.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.ScheduledFuture

@Component
class EmailOutboxRetryWakeupScheduler(
    private val taskScheduler: TaskScheduler,
    private val emailOutboxDomainService: EmailOutboxDomainService,
) {
    private val log = LoggerFactory.getLogger(EmailOutboxRetryWakeupScheduler::class.java)
    private val lock = Any()

    private var scheduledFuture: ScheduledFuture<*>? = null
    private var scheduledAt: LocalDateTime? = null

    fun scheduleEarliestRetry(onDue: () -> Unit) {
        val nextRetryAt = emailOutboxDomainService.findEarliestRetryAt() ?: run {
            clearSchedule()
            return
        }

        synchronized(lock) {
            val currentAt = scheduledAt
            if (currentAt != null && (nextRetryAt.isAfter(currentAt) || nextRetryAt.isEqual(currentAt))) {
                return
            }

            scheduledFuture?.cancel(false)
            val triggerAt = toInstant(nextRetryAt)
            scheduledFuture = taskScheduler.schedule(
                {
                    synchronized(lock) {
                        scheduledFuture = null
                        scheduledAt = null
                    }
                    onDue()
                },
                triggerAt
            )
            scheduledAt = nextRetryAt
            log.info("[EmailOutbox] scheduled retry wake-up at {}", nextRetryAt)
        }
    }

    private fun clearSchedule() {
        synchronized(lock) {
            scheduledFuture?.cancel(false)
            scheduledFuture = null
            scheduledAt = null
        }
    }

    private fun toInstant(dateTime: LocalDateTime): Instant {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant()
    }
}
