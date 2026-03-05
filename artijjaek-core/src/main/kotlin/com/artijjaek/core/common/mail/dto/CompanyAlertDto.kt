package com.artijjaek.core.common.mail.dto

import com.artijjaek.core.domain.company.entity.Company

data class CompanyAlertDto(
    val nameKr: String,
    val nameEn: String,
    val logo: String,
    val blogUrl: String,
) {

    companion object {
        fun from(company: Company): CompanyAlertDto {
            return CompanyAlertDto(
                nameKr = company.nameKr,
                nameEn = company.nameEn,
                logo = company.logo,
                blogUrl = company.blogUrl
            )
        }
    }
}
