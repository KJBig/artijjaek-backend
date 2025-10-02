package com.artiting.api.dto.response

import com.artiting.core.domain.Company

data class CompanySimpleDataResponse(
    val companyId: Long,
    val companyNameKr: String,
    val companyNameEn: String,
    val companyImageUrl: String,
) {
    companion object {
        fun from(company: Company): CompanySimpleDataResponse {
            return CompanySimpleDataResponse(
                companyId = requireNotNull(company.id) { "Company ID must not be null" },
                companyNameKr = company.nameKr,
                companyNameEn = company.nameEn,
                companyImageUrl = company.logo
            )
        }
    }
}
