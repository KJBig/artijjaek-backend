package com.artijjaek.core.common.error

import org.apache.http.HttpStatus.*

enum class ErrorCode(val code: String, val httpStatus: Int, val message: String) {

    // Admin Error
    ADMIN_NOT_FOUND_ERROR("ADE-1", SC_UNAUTHORIZED, "존재하지 않는 관리자입니다."),
    ADMIN_PASSWORD_NOT_MATCH_ERROR("ADE-2", SC_UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    ADMIN_NO_LOGIN_ERROR("ADE-3", SC_UNAUTHORIZED, "로그인하지 않은 관리자입니다."),

    // Article Error

    // Category Error
    CATEGORY_NOT_FOUND_ERROR("CAE-1", SC_NOT_FOUND, "존재하지 않는 카테고리입니다."),
    CATEGORY_ID_MISSING_ERROR("CAE-2", SC_INTERNAL_SERVER_ERROR, "카테고리 ID가 없습니다."),

    // Company Error
    COMPANY_NOT_FOUND_ERROR("COE-1", SC_NOT_FOUND, "존재하지 않는 회사입니다."),
    COMPANY_ID_MISSING_ERROR("COE-2", SC_INTERNAL_SERVER_ERROR, "회사 ID가 없습니다."),

    // Inquiry Error
    // Member Error
    MEMBER_NOT_FOUND_ERROR("MEE-1", SC_NOT_FOUND, "존재하지 않는 사용자입니다."),
    MEMBER_DUPLICATE_ERROR("MEE-2", SC_BAD_REQUEST, "이미 존재하는 이메일입니다."),
    MEMBER_TOKEN_NOT_MATCH_ERROR("MEE-3", SC_UNAUTHORIZED, "토큰이 일치하지 않습니다."),

    // Subscription Error
    // Unsubscription Error

    // Request Error
    API_NOT_FOUND_ERROR("REE-1", SC_NOT_FOUND, "존재하지 않는 API입니다."),
    JWT_NOT_FOUND_ERROR("REE-2", SC_UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    JWT_EXPIRATION_ERROR("REE-3", SC_UNAUTHORIZED, "만료된 토큰입니다."),
    JWT_INVALIDATE_ERROR("REE-4", SC_UNAUTHORIZED, "잘못된 토큰입니다."),
    JWT_NOT_MATCH_ERROR("REE-5", SC_UNAUTHORIZED, "토큰이 일치하지 않습니다."),

    // Server Error
    INTERNAL_SERVER_ERROR("SVE-1", SC_INTERNAL_SERVER_ERROR, "내부 서버 에러입니다.")

}