package com.artijjaek.api.dto.request

data class CheckMemberTokenAvailabilityRequest(
    val email: String,
    val token: String,
)
