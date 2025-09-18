package com.artiting.api.dto.common

data class SuccessDataResponse<T>(
    val isSuccess: Boolean = true,
    val message: String = "요청성공",
    val data: T,
)
