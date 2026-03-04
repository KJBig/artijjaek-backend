package com.artijjaek.admin.dto.response

import com.artijjaek.core.domain.mail.entity.EmailOutboxAttempt
import com.artijjaek.core.domain.mail.enums.EmailOutboxAttemptResult
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import java.time.LocalDateTime

data class MailOutboxAttemptSimpleResponse(
    val id: Long,
    val outboxId: Long,
    val mailType: EmailOutboxType,
    val recipientEmail: String,
    val subject: String,
    val status: EmailOutboxAttemptResult,
    val currentStatus: EmailOutboxStatus,
    val requestedBy: EmailOutboxRequestedBy,
    val attemptNo: Int,
    val errorMessage: String?,
    val occurredAt: LocalDateTime,
) {
    companion object {
        fun from(attempt: EmailOutboxAttempt): MailOutboxAttemptSimpleResponse {
            val outbox = attempt.emailOutbox
            return MailOutboxAttemptSimpleResponse(
                id = attempt.id!!,
                outboxId = outbox.id!!,
                mailType = outbox.mailType,
                recipientEmail = outbox.recipientEmail,
                subject = outbox.subject,
                status = attempt.result,
                currentStatus = outbox.status,
                requestedBy = attempt.requestedBy,
                attemptNo = attempt.attemptNo,
                errorMessage = attempt.errorMessage,
                occurredAt = attempt.occurredAt
            )
        }
    }
}
