package com.artijjaek.admin.dto.response

import java.time.LocalDate

data class ArticleDailyCollectedResponse(
    val date: LocalDate,
    val articleCount: Long,
)
