package com.artijjaek.core.domain.mail.dto

data class MemberSnapshot(
    val email: String,
    val nickname: String,
    val uuidToken: String,
)
