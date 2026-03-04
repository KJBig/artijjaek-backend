package com.artijjaek.core.domain.mail.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.mail.enums.EmailOutboxAttemptResult
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.time.LocalDateTime

@Entity
@Immutable
class EmailOutboxAttempt(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_outbox_attempt_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_outbox_id", nullable = false, updatable = false)
    var emailOutbox: EmailOutbox,

    @Column(name = "attempt_no", nullable = false, updatable = false)
    var attemptNo: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, updatable = false)
    var result: EmailOutboxAttemptResult,

    @Column(name = "error_message", length = 1000, updatable = false)
    var errorMessage: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_by", nullable = false, length = 20, updatable = false)
    var requestedBy: EmailOutboxRequestedBy,

    @Column(name = "occurred_at", nullable = false, updatable = false)
    var occurredAt: LocalDateTime = LocalDateTime.now(),
) : BaseEntity()
