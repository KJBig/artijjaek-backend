package com.artijjaek.core.domain.mail.service

import com.artijjaek.core.domain.mail.dto.DailyEmailSendAttemptCount
import com.artijjaek.core.domain.mail.entity.EmailOutboxAttempt
import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxAttemptResult
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.artijjaek.core.domain.mail.repository.EmailOutboxAttemptRepository
import com.artijjaek.core.domain.mail.repository.EmailOutboxRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmailOutboxDomainService(
    private val emailOutboxRepository: EmailOutboxRepository,
    private val emailOutboxAttemptRepository: EmailOutboxAttemptRepository,
) {
    fun save(emailOutbox: EmailOutbox): EmailOutbox {
        return emailOutboxRepository.save(emailOutbox)
    }

    fun saveAll(emailOutboxes: List<EmailOutbox>): List<EmailOutbox> {
        return emailOutboxRepository.saveAll(emailOutboxes)
    }

    fun findById(id: Long): EmailOutbox? {
        return emailOutboxRepository.findById(id).orElse(null)
    }

    fun saveAttempt(attempt: EmailOutboxAttempt): EmailOutboxAttempt {
        return emailOutboxAttemptRepository.save(attempt)
    }

    fun searchAttempts(
        pageable: Pageable,
        status: EmailOutboxAttemptResult?,
        requestedBy: EmailOutboxRequestedBy?,
        occurredAtFrom: LocalDateTime?,
        occurredAtTo: LocalDateTime?,
    ): Page<EmailOutboxAttempt> {
        return emailOutboxAttemptRepository.search(
            pageable = pageable,
            status = status,
            requestedBy = requestedBy,
            occurredAtFrom = occurredAtFrom,
            occurredAtTo = occurredAtTo
        )
    }

    fun findDueIds(now: LocalDateTime, limit: Int): List<Long> {
        return emailOutboxRepository.findDueIds(now, limit)
    }

    fun existsDue(now: LocalDateTime): Boolean {
        return emailOutboxRepository.existsDue(now)
    }

    fun claimForSending(id: Long, now: LocalDateTime): Boolean {
        return emailOutboxRepository.claimForSending(id, now)
    }

    fun markEnqueued(id: Long, now: LocalDateTime): Boolean {
        return emailOutboxRepository.markEnqueued(id, now)
    }

    fun findEarliestRetryAt(): LocalDateTime? {
        return emailOutboxRepository.findEarliestRetryAt()
    }

    fun search(
        pageable: Pageable,
        status: EmailOutboxStatus?,
        mailType: EmailOutboxType?,
        requestedBy: EmailOutboxRequestedBy?,
        recipientEmail: String?,
        requestedAtFrom: LocalDateTime?,
        requestedAtTo: LocalDateTime?,
    ): Page<EmailOutbox> {
        return emailOutboxRepository.search(
            pageable = pageable,
            status = status,
            mailType = mailType,
            requestedBy = requestedBy,
            recipientEmail = recipientEmail,
            requestedAtFrom = requestedAtFrom,
            requestedAtTo = requestedAtTo
        )
    }

    fun findOldestDueRequestedAt(now: LocalDateTime): LocalDateTime? {
        return emailOutboxRepository.findOldestDueRequestedAt(now)
    }

    fun countDailySuccessAttempts(
        startDateTime: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
        requestedBy: EmailOutboxRequestedBy?,
    ): List<DailyEmailSendAttemptCount> {
        return emailOutboxAttemptRepository.countDailySuccessAttempts(
            startDateTime = startDateTime,
            endDateTimeExclusive = endDateTimeExclusive,
            requestedBy = requestedBy
        )
    }

    fun countDailyFailureAttempts(
        startDateTime: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
        requestedBy: EmailOutboxRequestedBy?,
    ): List<DailyEmailSendAttemptCount> {
        return emailOutboxAttemptRepository.countDailyFailureAttempts(
            startDateTime = startDateTime,
            endDateTimeExclusive = endDateTimeExclusive,
            requestedBy = requestedBy
        )
    }
}
