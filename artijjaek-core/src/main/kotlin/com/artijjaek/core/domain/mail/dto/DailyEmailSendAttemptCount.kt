package com.artijjaek.core.domain.mail.dto

import java.time.LocalDate

data class DailyEmailSendAttemptCount(
    val date: LocalDate,
    val count: Long,
)
