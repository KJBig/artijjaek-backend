package com.artijjaek.core.domain.subscription.dto

data class TopSubscribedCompanyCount(
    val companyId: Long,
    val companyNameKr: String,
    val subscriberCount: Long,
)
