package com.artijjaek.core.domain.mail.repository

import com.artijjaek.core.domain.mail.dto.DailyEmailSendAttemptCount
import com.artijjaek.core.domain.mail.entity.EmailOutboxAttempt
import com.artijjaek.core.domain.mail.enums.EmailOutboxAttemptResult
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface EmailOutboxAttemptRepositoryCustom {
    fun search(
        pageable: Pageable,
        status: EmailOutboxAttemptResult?,
        requestedBy: EmailOutboxRequestedBy?,
        occurredAtFrom: LocalDateTime?,
        occurredAtTo: LocalDateTime?,
    ): Page<EmailOutboxAttempt>

    fun countDailySuccessAttempts(
        startDateTime: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
        requestedBy: EmailOutboxRequestedBy?,
    ): List<DailyEmailSendAttemptCount>

    fun countDailyFailureAttempts(
        startDateTime: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
        requestedBy: EmailOutboxRequestedBy?,
    ): List<DailyEmailSendAttemptCount>
}
