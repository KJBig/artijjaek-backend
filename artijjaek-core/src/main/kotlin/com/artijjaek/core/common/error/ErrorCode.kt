package com.artijjaek.core.common.error

import org.apache.http.HttpStatus
import org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import org.apache.http.HttpStatus.SC_NOT_FOUND

enum class ErrorCode(val code: String, val httpStatus: Int, val message: String) {

    // Article Error
    // Category Error
    CATEGORY_NOT_FOUND_ERROR("CAE-1", SC_NOT_FOUND, "존재하지 않는 카테고리입니다."),

    // Company Error
    // Inquiry Error
    // Member Error
    MEMBER_NOT_FOUND_ERROR("MEE-1", SC_NOT_FOUND, "존재하지 않는 사용자입니다."),
    MEMBER_DUPLICATE_ERROR("MEE-2", HttpStatus.SC_BAD_REQUEST, "이미 존재하는 이메일입니다."),
    MEMBER_TOKEN_NOT_MATCH_ERROR("MEE-3", HttpStatus.SC_UNAUTHORIZED, "토큰이 일치하지 않습니다."),

    // Subscription Error
    // Unsubscription Error

    // Request Error
    API_NOT_FOUND_ERROR("REE-1", SC_NOT_FOUND, "존재하지 않는 API입니다."),

    // Server Error
    INTERNAL_SERVER_ERROR("SVE-1", SC_INTERNAL_SERVER_ERROR, "내부 서버 에러입니다.")

}