package com.artijjaek.admin.dto.response

import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import java.time.LocalDateTime

data class MailOutboxSimpleResponse(
    val id: Long,
    val mailType: EmailOutboxType,
    val recipientEmail: String,
    val subject: String,
    val status: EmailOutboxStatus,
    val requestedBy: EmailOutboxRequestedBy,
    val attemptCount: Int,
    val maxAttempts: Int,
    val lastError: String?,
    val requestedAt: LocalDateTime,
    val nextRetryAt: LocalDateTime?,
    val sentAt: LocalDateTime?,
) {
    companion object {
        fun from(outbox: EmailOutbox): MailOutboxSimpleResponse {
            return MailOutboxSimpleResponse(
                id = outbox.id!!,
                mailType = outbox.mailType,
                recipientEmail = outbox.recipientEmail,
                subject = outbox.subject,
                status = outbox.status,
                requestedBy = outbox.requestedBy,
                attemptCount = outbox.attemptCount,
                maxAttempts = outbox.maxAttempts,
                lastError = outbox.lastError,
                requestedAt = outbox.requestedAt,
                nextRetryAt = outbox.nextRetryAt,
                sentAt = outbox.sentAt
            )
        }
    }
}
