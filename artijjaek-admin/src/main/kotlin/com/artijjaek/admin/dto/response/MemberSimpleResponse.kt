package com.artijjaek.admin.dto.response

import com.artijjaek.core.domain.member.enums.MemberStatus
import java.time.LocalDateTime

data class MemberSimpleResponse(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val memberStatus: MemberStatus,
    val createdAt: LocalDateTime
)
