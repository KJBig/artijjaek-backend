package com.artijjaek.core.domain.mail.queue.trigger

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(MailDispatchTrigger::class)
class NoOpMailDispatchTrigger : MailDispatchTrigger {
    override fun dispatchOutbox(outboxId: Long) {
    }

    override fun dispatchDueOutboxes(limit: Int) {
    }
}
