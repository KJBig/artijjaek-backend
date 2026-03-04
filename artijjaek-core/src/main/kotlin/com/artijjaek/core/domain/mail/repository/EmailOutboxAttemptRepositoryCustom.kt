package com.artijjaek.core.domain.mail.repository

import com.artijjaek.core.domain.mail.dto.DailyEmailSendAttemptCount
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import java.time.LocalDateTime

interface EmailOutboxAttemptRepositoryCustom {
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
