package com.artiting.api.dto.common

data class SuccessResponse(
    val isSuccess: Boolean = true,
    val message: String = "요청성공",
)
