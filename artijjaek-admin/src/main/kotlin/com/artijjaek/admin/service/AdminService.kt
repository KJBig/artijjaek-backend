package com.artijjaek.admin.service

import com.artijjaek.admin.common.util.PasswordEncoder
import com.artijjaek.admin.dto.request.LoginRequest
import com.artijjaek.admin.dto.request.RefreshRequest
import com.artijjaek.admin.dto.response.LoginResponse
import com.artijjaek.admin.dto.response.RefreshResponse
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.*
import com.artijjaek.core.common.jwt.JwtProvider
import com.artijjaek.core.domain.admin.service.AdminDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val adminDomainService: AdminDomainService,
    private val jwtProvider: JwtProvider,
) {

    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        // 비밀번호 체크
        val admin = adminDomainService.findByEmail(request.email) ?: throw IllegalStateException()
        val isMatch = PasswordEncoder.isMatch(request.password, admin.password)

        if (!isMatch) {
            throw ApplicationException(ADMIN_PASSWORD_NOT_MATCH_ERROR)
        }

        // 토큰 생성
        val accessToken = jwtProvider.generateAccessToken(admin.id!!, admin.adminRole.name)
        val refreshToken = jwtProvider.generateRefreshToken()

        admin.changeRefreshToken(refreshToken)

        return LoginResponse(accessToken, refreshToken)
    }

    @Transactional(readOnly = true)
    fun refreshAccessToken(request: RefreshRequest): RefreshResponse {
        // Refresh Token 검증
        jwtProvider.validateToken(request.refreshToken)

        val adminId = jwtProvider.parseAccessToken(request.accessToken).subject.toLong()
        val admin = adminDomainService.findById(adminId)

        if (admin.refreshToken == null) {
            throw ApplicationException(ADMIN_NO_LOGIN_ERROR)
        }

        if (!admin.refreshToken.equals(request.refreshToken)) {
            throw ApplicationException(JWT_NOT_MATCH_ERROR)
        }

        val newAccessToken = jwtProvider.generateAccessToken(admin.id!!, admin.adminRole.name)

        return RefreshResponse(newAccessToken, request.refreshToken)
    }

}