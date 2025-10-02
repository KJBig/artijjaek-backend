package com.artijjaek.api.dto.request

data class ChangeSubscribeRequest(
    val email: String,
    val token: String,
    val companyIds: List<Long>,
)