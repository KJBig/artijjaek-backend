package com.artiting.api.dto.response

data class CompanySimpleDataResponse(
    val companyId: Long,
    val companyNameKr: String,
    val companyNameEn: String,
    val companyImageUrl: String,
)
