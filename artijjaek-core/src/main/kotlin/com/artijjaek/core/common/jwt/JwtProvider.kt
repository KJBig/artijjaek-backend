package com.artijjaek.core.common.jwt

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtProvider(
    @Value("\${jwt.secret-key}")
    private var secretKey: String,

    @Value("\${jwt.expiration-time.access}")
    private var accessTokenExpirationTime: Long,

    @Value("\${jwt.expiration-time.refresh}")
    private var refreshTokenExpirationTime: Long,

    ) {

    private val log = LoggerFactory.getLogger(JwtProvider::class.java)

    private val signingKey: SecretKey by lazy {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateAccessToken(memberId: Long, role: String?): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenExpirationTime)

        val builder = Jwts.builder()
            .subject(memberId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .claim("type", "ACCESS")

        if (!role.isNullOrBlank()) {
            builder.claim("role", role)
        }

        return builder
            .signWith(signingKey)
            .compact()
    }

    fun generateRefreshToken(): String {
        val now = Date()
        val expiry = Date(now.time + refreshTokenExpirationTime)

        val builder = Jwts.builder()
            .issuedAt(now)
            .expiration(expiry)
            .claim("type", "REFRESH")

        return builder
            .signWith(signingKey)
            .compact()
    }

    fun parseAccessToken(accessToken: String): AccessTokenInfo {
        val claims = parseAndValidate(accessToken)

        val subject = claims.subject ?: throw ApplicationException(ErrorCode.JWT_INVALIDATE_ERROR)

        val roles = when (val raw = claims["roles"]) {
            is List<*> -> raw.filterIsInstance<String>()
            is String -> listOf(raw)
            else -> emptyList()
        }

        return AccessTokenInfo(
            subject = subject,
            roles = roles,
            issuedAt = claims.issuedAt?.time,
            expiresAt = claims.expiration?.time
        )
    }

    private fun parseAndValidate(token: String): Claims {
        try {
            val parser = Jwts.parser()
                .verifyWith(signingKey)
                .build()

            return parser.parseSignedClaims(token).payload
        } catch (e: ExpiredJwtException) {
            log.error("JWT 만료 : ${e.message}", e)
            throw ApplicationException(ErrorCode.JWT_EXPIRATION_ERROR)
        } catch (e: JwtException) {
            log.error("유효하지 않은 JWT: ${e.message}", e)
            throw ApplicationException(ErrorCode.JWT_INVALIDATE_ERROR)
        } catch (e: IllegalArgumentException) {
            log.error("유효하지 않은 JWT", e)
            throw ApplicationException(ErrorCode.JWT_NOT_FOUND_ERROR)
        } catch (e: Exception) {
            log.error("JWT parse error", e)
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    fun validateToken(refreshToken: String) {
        parseAndValidate(refreshToken)
    }

}