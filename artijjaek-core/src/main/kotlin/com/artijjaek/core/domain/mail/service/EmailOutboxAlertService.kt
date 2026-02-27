package com.artijjaek.core.domain.mail.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class EmailOutboxAlertService(
    private val emailOutboxDomainService: EmailOutboxDomainService,
) {
    private val log = LoggerFactory.getLogger(EmailOutboxAlertService::class.java)
    private val lock = Any()

    private var windowStart: LocalDateTime = LocalDateTime.now()
    private var successCount: Long = 0
    private var failureCount: Long = 0

    fun recordSuccess() {
        synchronized(lock) {
            rollWindowIfNeeded(LocalDateTime.now())
            successCount++
        }
    }

    fun recordFailure() {
        synchronized(lock) {
            rollWindowIfNeeded(LocalDateTime.now())
            failureCount++
        }
    }

    fun notifyDead(outboxId: Long?, lastError: String?) {
        log.error("[EmailOutbox][Critical] DEAD 발생 id={}, error={}", outboxId, lastError)
    }

    fun checkBacklog(now: LocalDateTime = LocalDateTime.now()) {
        val oldestDue = emailOutboxDomainService.findOldestDueRequestedAt(now) ?: return
        val delayedMinutes = Duration.between(oldestDue, now).toMinutes()
        if (delayedMinutes >= 5) {
            log.warn(
                "[EmailOutbox][Backlog] due queue 지연 감지 oldestRequestedAt={}, delayedMinutes={}",
                oldestDue,
                delayedMinutes
            )
        }
    }

    private fun rollWindowIfNeeded(now: LocalDateTime) {
        val elapsedMinutes = Duration.between(windowStart, now).toMinutes()
        if (elapsedMinutes < 10) {
            return
        }

        val total = successCount + failureCount
        if (total > 0) {
            val failureRate = failureCount.toDouble() / total.toDouble() * 100.0
            when {
                failureRate > 25.0 -> log.error(
                    "[EmailOutbox][Quality] 실패율 치명: failureRate={}%, success={}, failure={}, windowStart={}, windowEnd={}",
                    String.format("%.2f", failureRate), successCount, failureCount, windowStart, now
                )

                failureRate > 10.0 -> log.warn(
                    "[EmailOutbox][Quality] 실패율 경고: failureRate={}%, success={}, failure={}, windowStart={}, windowEnd={}",
                    String.format("%.2f", failureRate), successCount, failureCount, windowStart, now
                )
            }
        }

        windowStart = now
        successCount = 0
        failureCount = 0
    }
}
