package com.artijjaek.core.common.error

class ApplicationException(
    val code: String, val httpStatus: Int, override val message: String
) : RuntimeException(message) {
    constructor(errorCode: ErrorCode) : this(errorCode.code, errorCode.httpStatus, errorCode.message)
}