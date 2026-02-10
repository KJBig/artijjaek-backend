package com.artijjaek.admin.dto.response

data class MemberSubscribedCompanyResponse(
    val companyId: Long,
    val companyNameKr: String,
    val companyNameEn: String,
    val logo: String,
)
