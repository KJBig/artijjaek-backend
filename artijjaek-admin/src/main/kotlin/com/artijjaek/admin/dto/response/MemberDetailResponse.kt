package com.artijjaek.admin.dto.response

import com.artijjaek.core.domain.member.enums.MemberStatus

data class MemberDetailResponse(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val memberStatus: MemberStatus,
    val subscribedCompanies: List<MemberSubscribedCompanyResponse>,
    val subscribedCategories: List<MemberSubscribedCategoryResponse>,
)
