package com.artijjaek.core.domain.mail.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class EmailOutbox(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_outbox_id")
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "mail_type", nullable = false, length = 30)
    var mailType: EmailOutboxType,

    @Column(name = "recipient_email", nullable = false, length = 255)
    var recipientEmail: String,

    @Column(nullable = false, length = 255)
    var subject: String,

    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    var payloadJson: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: EmailOutboxStatus = EmailOutboxStatus.PENDING,

    @Column(name = "attempt_count", nullable = false)
    var attemptCount: Int = 0,

    @Column(name = "max_attempts", nullable = false)
    var maxAttempts: Int = 5,

    @Column(name = "next_retry_at")
    var nextRetryAt: LocalDateTime? = null,

    @Column(name = "last_error", length = 1000)
    var lastError: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_by", nullable = false, length = 20)
    var requestedBy: EmailOutboxRequestedBy,

    @Column(name = "requested_at", nullable = false)
    var requestedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "sent_at")
    var sentAt: LocalDateTime? = null,
) : BaseEntity()
