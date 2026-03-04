package com.artijjaek.mq.processor

import com.artijjaek.core.domain.mail.queue.trigger.MailDispatchTrigger
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OutboxDuePollingScheduler(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val mailDispatchTrigger: MailDispatchTrigger,
) {

    @Scheduled(fixedDelay = 60000)
    fun triggerWhenDueExists() {
        if (emailOutboxDomainService.existsDue(LocalDateTime.now())) {
            mailDispatchTrigger.dispatchDueOutboxes()
        }
    }
}
