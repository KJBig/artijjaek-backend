package com.artijjaek.admin.config.security.filter

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.JWT_NOT_FOUND_ERROR
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        if (request.getHeader("Authorization") == null) {
            throw ApplicationException(JWT_NOT_FOUND_ERROR)
        }
    }

}