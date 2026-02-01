package com.artijjaek.admin.dto.common

data class SuccessDataResponse<T>(
    val isSuccess: Boolean,
    val message: String,
    val data: T,
) {
    constructor(data: T) : this(true, "요청성공", data)
}
