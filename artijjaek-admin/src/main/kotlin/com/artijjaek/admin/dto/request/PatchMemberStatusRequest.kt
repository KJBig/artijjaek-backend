package com.artijjaek.admin.dto.request

import com.artijjaek.core.domain.member.enums.MemberStatus

data class PatchMemberStatusRequest(
    val memberStatus: MemberStatus,
)
