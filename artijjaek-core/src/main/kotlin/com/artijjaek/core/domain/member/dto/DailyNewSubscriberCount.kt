package com.artijjaek.core.domain.member.dto

import java.time.LocalDate

data class DailyNewSubscriberCount(
    val date: LocalDate,
    val count: Long,
)
