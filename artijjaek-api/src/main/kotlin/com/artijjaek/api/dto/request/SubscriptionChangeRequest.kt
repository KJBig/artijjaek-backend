package com.artijjaek.api.dto.request

data class SubscriptionChangeRequest(
    val email: String,
    val token: String,
    val companyIds: List<Long>,
)