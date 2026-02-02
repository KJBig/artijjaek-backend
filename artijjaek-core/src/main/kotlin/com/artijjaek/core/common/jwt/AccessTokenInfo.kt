package com.artijjaek.core.common.jwt

data class AccessTokenInfo(
    val subject: String,
    val roles: List<String>,
    val issuedAt: Long?,
    val expiresAt: Long?
)