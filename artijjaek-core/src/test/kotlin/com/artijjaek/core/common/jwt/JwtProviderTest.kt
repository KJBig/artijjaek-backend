package com.artijjaek.core.common.jwt

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import java.util.*
import javax.crypto.SecretKey
import kotlin.test.Test

class JwtProviderTest {

    lateinit var jwtProvider: JwtProvider

    private val secretKeyBase64 = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="

    @BeforeEach
    fun setUp() {
        jwtProvider = JwtProvider(
            secretKey = secretKeyBase64,
            accessTokenExpirationTime = 60_000L,
            refreshTokenExpirationTime = 120_000L
        )
    }

    @Test
    @DisplayName("Access Token을 memberId와 role로 생성할 수 있다")
    fun generateAccessTokenTest() {
        // given
        val memberId = 42L
        val role = "ADMIN"


        // when
        val token = jwtProvider.generateAccessToken(memberId, role)
        val info = jwtProvider.parseAccessToken(token)


        // then
        assertThat(info.subject).isEqualTo(memberId.toString())
        assertThat(info.roles).containsExactly("ADMIN")
        assertThat(info.issuedAt).isNotNull
        assertThat(info.expiresAt).isNotNull
        assertThat(info.expiresAt!!).isGreaterThan(info.issuedAt!!)
    }

    @Test
    @DisplayName("Access Token을 memberId와 role로 생성할 수 있다 - 역할이 없으면 roles는 빈 리스트로 생성된다")
    fun generateAccessTokenTest_roleIsNullOrBlank() {
        // given
        val memberId = 1L


        // when
        val token1 = jwtProvider.generateAccessToken(memberId, null)
        val token2 = jwtProvider.generateAccessToken(memberId, " ")

        val info1 = jwtProvider.parseAccessToken(token1)
        val info2 = jwtProvider.parseAccessToken(token2)


        // then
        assertThat(info1.roles).isEmpty()
        assertThat(info2.roles).isEmpty()
    }

    @Test
    @DisplayName("Access Token을 해석해서 정보를 얻을 수 있다")
    fun parseAccessTokenTest() {
        // given
        val accessToken = jwtProvider.generateAccessToken(1L, "USER")

        // when
        val result = jwtProvider.parseAccessToken(accessToken)


        // then
        assertThat(result.subject).isEqualTo("1")
        assertThat(result.roles[0]).isEqualTo("USER")
    }

    @Test
    @DisplayName("Access Token이 만료된 토큰이면 JWT_EXPIRATION_ERROR 예외를 던진다")
    fun parseAccessTokenTest_expired() {
        // given
        val signingKey: SecretKey =
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyBase64))

        val expiredToken = Jwts.builder()
            .subject("1")
            .issuedAt(Date(System.currentTimeMillis() - 10_000L))
            .expiration(Date(System.currentTimeMillis() - 1_000L))
            .claim("type", "ACCESS")
            .claim("roles", listOf("USER"))
            .signWith(signingKey)
            .compact()


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            jwtProvider.parseAccessToken(expiredToken)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.JWT_EXPIRATION_ERROR.code)
    }

    @Test
    @DisplayName("서명이 다르면 JWT_INVALIDATE_ERROR 예외를 던진다")
    fun parseAccessTokenTest_invalidSignature() {
        // given
        val invalidKeyBase64 = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXphYmNkZWY="
        val invalidSigningKey: SecretKey =
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(invalidKeyBase64))

        val invalidSignatureToken = Jwts.builder()
            .subject("1")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 60_000L))
            .claim("type", "ACCESS")
            .claim("roles", listOf("USER"))
            .signWith(invalidSigningKey)
            .compact()


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            jwtProvider.parseAccessToken(invalidSignatureToken)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.JWT_INVALIDATE_ERROR.code)
    }

    @Test
    @DisplayName("Refresh Token을 생성할 수 있다")
    fun generateRefreshTokenTest() {
        // when
        val refreshToken = jwtProvider.generateRefreshToken()

        // then
        jwtProvider.validateToken(refreshToken)
    }

    @Test
    @DisplayName("validateToken - 변조된 토큰이면 JWT_INVALIDATE_ERROR 예외를 던진다")
    fun validateTokenTest_tampered() {
        // given
        val refreshToken = jwtProvider.generateRefreshToken()

        val tampered = refreshToken.dropLast(1) + if (refreshToken.last() != 'a') 'a' else 'b'


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            jwtProvider.validateToken(tampered)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.JWT_INVALIDATE_ERROR.code)
    }
}