package com.artijjaek.api.dto.request

data class SubscriptionChangeRequest(
    val email: String,
    val token: String,
    val nickname: String,
    val categoryIds: List<Long>,
    val companyIds: List<Long>,
)