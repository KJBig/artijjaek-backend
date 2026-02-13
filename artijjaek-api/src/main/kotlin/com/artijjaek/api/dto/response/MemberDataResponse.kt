package com.artijjaek.api.dto.response

import com.artijjaek.core.domain.member.entity.Member

data class MemberDataResponse(
    val email: String,
    val nickname: String,
    val companies: List<CompanySimpleDataResponse>,
    val categories: List<CategorySimpleDataResponse>,
) {
    companion object {
        fun of(
            member: Member,
            companyIds: List<CompanySimpleDataResponse>,
            categoryIds: List<CategorySimpleDataResponse>
        ): MemberDataResponse {
            return MemberDataResponse(
                email = member.email,
                nickname = member.nickname,
                companies = companyIds,
                categories = categoryIds,
            )
        }
    }
}
