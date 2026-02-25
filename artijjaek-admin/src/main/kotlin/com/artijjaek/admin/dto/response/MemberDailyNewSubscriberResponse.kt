package com.artijjaek.admin.dto.response

import java.time.LocalDate

data class MemberDailyNewSubscriberResponse(
    val date: LocalDate,
    val subscriberCount: Long,
)
