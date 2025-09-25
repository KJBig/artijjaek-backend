package com.artiting.api.dto.request

data class CheckMemberTokenAvailabilityRequest(
    val email: String,
    val token: String,
)
