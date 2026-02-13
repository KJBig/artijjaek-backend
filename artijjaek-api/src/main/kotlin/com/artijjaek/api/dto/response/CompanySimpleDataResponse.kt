package com.artijjaek.api.dto.response

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import com.artijjaek.core.domain.company.entity.Company

data class CompanySimpleDataResponse(
    val companyId: Long,
    val companyNameKr: String,
    val companyNameEn: String,
    val companyImageUrl: String,
    val companyBlogUrl: String,
) {
    companion object {
        fun from(company: Company): CompanySimpleDataResponse {
            return CompanySimpleDataResponse(
                companyId = requireNotNull(company.id) { throw ApplicationException(ErrorCode.COMPANY_ID_MISSING_ERROR) },
                companyNameKr = company.nameKr,
                companyNameEn = company.nameEn,
                companyImageUrl = company.logo,
                companyBlogUrl = company.blogUrl,
            )
        }
    }
}
