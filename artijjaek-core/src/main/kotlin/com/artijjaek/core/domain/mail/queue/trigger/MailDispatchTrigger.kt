package com.artijjaek.core.domain.mail.queue.trigger

interface MailDispatchTrigger {
    fun dispatchOutbox(outboxId: Long)
    fun dispatchDueOutboxes(limit: Int = 200)
}
