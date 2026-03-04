package com.artijjaek.admin.dto.response

import java.time.LocalDate

data class MailDailyFailedCountResponse(
    val date: LocalDate,
    val failedCount: Long,
)
