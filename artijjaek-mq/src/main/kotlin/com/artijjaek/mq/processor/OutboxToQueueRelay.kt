package com.artijjaek.mq.processor

import com.artijjaek.core.domain.mail.queue.trigger.MailDispatchTrigger
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.mq.queue.MailQueueChannel
import com.artijjaek.mq.queue.MailQueueMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean

@Component
class OutboxToQueueRelay(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val mailQueueChannel: MailQueueChannel,
    @Qualifier("mailRelayExecutor")
    private val relayExecutor: ThreadPoolTaskExecutor,
) : MailDispatchTrigger {
    private val log = LoggerFactory.getLogger(OutboxToQueueRelay::class.java)
    private val running = AtomicBoolean(false)

    override fun dispatchOutbox(outboxId: Long) {
        mailQueueChannel.publish(MailQueueMessage(outboxId = outboxId))
        emailOutboxDomainService.markEnqueued(outboxId, LocalDateTime.now())
    }

    override fun dispatchDueOutboxes(limit: Int) {
        val relayStarted = running.compareAndSet(false, true)
        if (!relayStarted) {
            return
        }

        relayExecutor.submit {
            try {
                relayLoop(limit)
            } finally {
                running.set(false)
            }
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        dispatchDueOutboxes()
    }

    private fun relayLoop(limit: Int) {
        while (true) {
            val now = LocalDateTime.now()
            val dueIds = emailOutboxDomainService.findDueIds(now, limit)
            if (dueIds.isEmpty()) {
                return
            }

            dueIds.forEach { id ->
                mailQueueChannel.publish(MailQueueMessage(outboxId = id))
                emailOutboxDomainService.markEnqueued(id, now)
            }
            log.info("[MailRelay] published count={}", dueIds.size)

            if (dueIds.size < limit) {
                return
            }
        }
    }
}
