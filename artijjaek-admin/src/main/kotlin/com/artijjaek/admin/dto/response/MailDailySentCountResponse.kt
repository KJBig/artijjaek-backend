package com.artijjaek.admin.dto.response

import java.time.LocalDate

data class MailDailySentCountResponse(
    val date: LocalDate,
    val sentCount: Long,
)
