package com.artiting.api.dto.request

data class RegisterMemberRequest(
    val email: String,
    val nickname: String,
    val companyIds: List<Long>,
)
