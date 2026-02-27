package com.artijjaek.core.domain.mail.service

import com.artijjaek.core.domain.mail.event.MailQueuedEvent
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime

@Component
class EmailOutboxEventHandlers(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val emailOutboxWorkerCoordinator: EmailOutboxWorkerCoordinator,
    private val retryWakeupScheduler: EmailOutboxRetryWakeupScheduler,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    fun onMailQueued(@Suppress("UNUSED_PARAMETER") event: MailQueuedEvent) {
        emailOutboxWorkerCoordinator.triggerProcessing()
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        if (emailOutboxDomainService.existsDue(LocalDateTime.now())) {
            emailOutboxWorkerCoordinator.triggerProcessing()
            return
        }
        retryWakeupScheduler.scheduleEarliestRetry { emailOutboxWorkerCoordinator.triggerProcessing() }
    }
}
