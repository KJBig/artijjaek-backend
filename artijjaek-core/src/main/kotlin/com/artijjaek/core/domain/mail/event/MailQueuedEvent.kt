package com.artijjaek.core.domain.mail.event

data class MailQueuedEvent(
    val outboxId: Long,
)
