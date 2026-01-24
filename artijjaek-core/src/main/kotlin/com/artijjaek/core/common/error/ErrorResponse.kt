package com.artijjaek.core.common.error

data class ErrorResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
) {
    constructor(code: String, message: String) : this(false, code, message)
}