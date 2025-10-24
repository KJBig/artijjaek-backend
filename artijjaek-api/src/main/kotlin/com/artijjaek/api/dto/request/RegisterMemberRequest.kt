package com.artijjaek.api.dto.request

data class RegisterMemberRequest(
    val email: String,
    val nickname: String,
    val categoryIds: List<Long>,
    val companyIds: List<Long>,
)
