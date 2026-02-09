package com.artijjaek.admin.service

import com.artijjaek.admin.common.util.PasswordEncoder
import com.artijjaek.admin.dto.request.LoginRequest
import com.artijjaek.admin.dto.request.RefreshRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.*
import com.artijjaek.core.common.jwt.AccessTokenInfo
import com.artijjaek.core.common.jwt.JwtProvider
import com.artijjaek.core.domain.admin.entity.Admin
import com.artijjaek.core.domain.admin.enums.AdminRole
import com.artijjaek.core.domain.admin.service.AdminDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AuthServiceTest {

    @InjectMockKs
    lateinit var authService: AuthService

    @MockK
    lateinit var adminDomainService: AdminDomainService

    @MockK
    lateinit var adminRefreshTokenService: AdminRefreshTokenService

    @MockK
    lateinit var jwtProvider: JwtProvider

    @Test
    @DisplayName("로그인에 성공하면 액세스 토큰과 리프레시 토큰을 반환한다")
    fun loginTest() {
        // given
        val request = LoginRequest("admin@test.com", "password123")
        val admin = Admin(
            id = 1L,
            name = "관리자",
            email = "admin@test.com",
            password = PasswordEncoder.passwordEncode("password123"),
            adminRole = AdminRole.SUPER_ADMIN,
            refreshToken = null
        )
        every { adminDomainService.findByEmail("admin@test.com") } returns admin
        every { jwtProvider.generateAccessToken(1L, AdminRole.SUPER_ADMIN.name) } returns "access-token"
        every { jwtProvider.generateRefreshToken() } returns "refresh-token"

        // when
        val response = authService.login(request)

        // then
        assertThat(response.accessToken).isEqualTo("access-token")
        assertThat(response.refreshToken).isEqualTo("refresh-token")
        assertThat(admin.refreshToken).isEqualTo("refresh-token")
    }

    @Test
    @DisplayName("존재하지 않는 관리자 이메일이면 예외가 발생한다")
    fun loginAdminNotFoundTest() {
        // given
        val request = LoginRequest("none@test.com", "password123")
        every { adminDomainService.findByEmail("none@test.com") } returns null

        // when
        val exception = assertThrows<ApplicationException> {
            authService.login(request)
        }

        // then
        assertThat(exception.code).isEqualTo(ADMIN_NOT_FOUND_ERROR.code)
        assertThat(exception.message).isEqualTo(ADMIN_NOT_FOUND_ERROR.message)
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
    fun loginPasswordNotMatchTest() {
        // given
        val request = LoginRequest("admin@test.com", "wrong-password")
        val admin = Admin(
            id = 1L,
            name = "관리자",
            email = "admin@test.com",
            password = PasswordEncoder.passwordEncode("password123"),
            adminRole = AdminRole.SUPER_ADMIN,
            refreshToken = null
        )
        every { adminDomainService.findByEmail("admin@test.com") } returns admin

        // when
        val exception = assertThrows<ApplicationException> {
            authService.login(request)
        }

        // then
        assertThat(exception.code).isEqualTo(ADMIN_PASSWORD_NOT_MATCH_ERROR.code)
        assertThat(exception.message).isEqualTo(ADMIN_PASSWORD_NOT_MATCH_ERROR.message)
    }

    @Test
    @DisplayName("리프레시 토큰이 유효하면 액세스 토큰을 재발급한다")
    fun refreshAccessTokenTest() {
        // given
        val request = RefreshRequest(
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )
        val admin = Admin(
            id = 1L,
            name = "관리자",
            email = "admin@test.com",
            password = "encoded-password",
            adminRole = AdminRole.SUPER_ADMIN,
            refreshToken = "refresh-token"
        )
        every { jwtProvider.parseAccessToken("access-token") } returns AccessTokenInfo("1", emptyList(), null, null)
        every { jwtProvider.validateToken("refresh-token") } returns Unit
        every { adminDomainService.findById(1L) } returns admin
        every { jwtProvider.generateAccessToken(1L, AdminRole.SUPER_ADMIN.name) } returns "new-access-token"

        // when
        val response = authService.refreshAccessToken(request)

        // then
        assertThat(response.accessToken).isEqualTo("new-access-token")
        assertThat(response.refreshToken).isEqualTo("refresh-token")
    }

    @Test
    @DisplayName("로그인 상태가 아니면 리프레시 토큰 재발급 시 예외가 발생한다")
    fun refreshAccessTokenNoLoginTest() {
        // given
        val request = RefreshRequest(
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )
        val admin = Admin(
            id = 1L,
            name = "관리자",
            email = "admin@test.com",
            password = "encoded-password",
            adminRole = AdminRole.SUPER_ADMIN,
            refreshToken = null
        )
        every { jwtProvider.parseAccessToken("access-token") } returns AccessTokenInfo("1", emptyList(), null, null)
        every { jwtProvider.validateToken("refresh-token") } returns Unit
        every { adminDomainService.findById(1L) } returns admin

        // when
        val exception = assertThrows<ApplicationException> {
            authService.refreshAccessToken(request)
        }

        // then
        assertThat(exception.code).isEqualTo(ADMIN_NO_LOGIN_ERROR.code)
        assertThat(exception.message).isEqualTo(ADMIN_NO_LOGIN_ERROR.message)
    }

    @Test
    @DisplayName("리프레시 토큰이 저장된 토큰과 다르면 예외가 발생한다")
    fun refreshAccessTokenNotMatchTest() {
        // given
        val request = RefreshRequest(
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )
        val admin = Admin(
            id = 1L,
            name = "관리자",
            email = "admin@test.com",
            password = "encoded-password",
            adminRole = AdminRole.SUPER_ADMIN,
            refreshToken = "other-refresh-token"
        )
        every { jwtProvider.parseAccessToken("access-token") } returns AccessTokenInfo("1", emptyList(), null, null)
        every { jwtProvider.validateToken("refresh-token") } returns Unit
        every { adminDomainService.findById(1L) } returns admin

        // when
        val exception = assertThrows<ApplicationException> {
            authService.refreshAccessToken(request)
        }

        // then
        assertThat(exception.code).isEqualTo(JWT_NOT_MATCH_ERROR.code)
        assertThat(exception.message).isEqualTo(JWT_NOT_MATCH_ERROR.message)
    }

    @Test
    @DisplayName("리프레시 토큰이 만료되면 토큰을 비우고 예외가 발생한다")
    fun refreshAccessTokenExpirationTest() {
        // given
        val request = RefreshRequest(
            accessToken = "access-token",
            refreshToken = "expired-refresh-token"
        )
        every { jwtProvider.parseAccessToken("access-token") } returns AccessTokenInfo("1", emptyList(), null, null)
        every { jwtProvider.validateToken("expired-refresh-token") } throws ApplicationException(JWT_EXPIRATION_ERROR)
        justRun { adminRefreshTokenService.clearRefreshToken(1L) }

        // when
        val exception = assertThrows<ApplicationException> {
            authService.refreshAccessToken(request)
        }

        // then
        assertThat(exception.code).isEqualTo(JWT_EXPIRATION_ERROR.code)
        assertThat(exception.message).isEqualTo(JWT_EXPIRATION_ERROR.message)
        verify(exactly = 1) { adminRefreshTokenService.clearRefreshToken(1L) }
    }

    @Test
    @DisplayName("로그아웃하면 리프레시 토큰을 비운다")
    fun logoutTest() {
        // given
        val admin = Admin(
            id = 1L,
            name = "관리자",
            email = "admin@test.com",
            password = "encoded-password",
            adminRole = AdminRole.SUPER_ADMIN,
            refreshToken = "refresh-token"
        )
        every { adminDomainService.findById(1L) } returns admin

        // when
        authService.logout(1L)

        // then
        assertThat(admin.refreshToken).isNull()
    }
}
