package com.artijjaek.core.domain.article.dto

import java.time.LocalDate

data class DailyCollectedArticleCount(
    val date: LocalDate,
    val count: Long,
)
