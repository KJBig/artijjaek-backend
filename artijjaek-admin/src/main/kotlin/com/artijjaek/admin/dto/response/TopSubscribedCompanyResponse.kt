package com.artijjaek.admin.dto.response

data class TopSubscribedCompanyResponse(
    val rank: Int,
    val companyId: Long,
    val companyNameKr: String,
    val subscriberCount: Long,
)
