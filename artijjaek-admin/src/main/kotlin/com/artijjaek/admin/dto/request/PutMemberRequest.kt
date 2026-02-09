package com.artijjaek.admin.dto.request

data class PutMemberRequest(
    val email: String,
    val nickname: String,
    val companyIds: List<Long>,
    val categoryIds: List<Long>,
)
