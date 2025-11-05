package com.artijjaek.api.dto.response

import com.artijjaek.core.domain.member.entity.Member

data class MemberDataResponse(
    val email: String,
    val nickname: String,
    val companyIds: List<Long>,
    val categoryIds: List<Long>,
) {
    companion object {
        fun of(member: Member, companyIds: List<Long>, categoryIds: List<Long>): MemberDataResponse {
            return MemberDataResponse(
                email = member.email,
                nickname = member.nickname,
                companyIds = companyIds,
                categoryIds = categoryIds,
            )
        }
    }
}
