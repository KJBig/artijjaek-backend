package com.artijjaek.core.domain.mail.repository

import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface EmailOutboxRepositoryCustom {
    fun findDueIds(now: LocalDateTime, limit: Int): List<Long>
    fun existsDue(now: LocalDateTime): Boolean
    fun findEarliestRetryAt(): LocalDateTime?
    fun claimForSending(id: Long, now: LocalDateTime): Boolean
    fun markEnqueued(id: Long, now: LocalDateTime): Boolean
    fun search(
        pageable: Pageable,
        status: EmailOutboxStatus?,
        mailType: EmailOutboxType?,
        requestedBy: EmailOutboxRequestedBy?,
        recipientEmail: String?,
        requestedAtFrom: LocalDateTime?,
        requestedAtTo: LocalDateTime?,
    ): Page<EmailOutbox>

    fun findOldestDueRequestedAt(now: LocalDateTime): LocalDateTime?
}
