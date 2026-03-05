package com.artijjaek.core.domain.mail.dto

import com.artijjaek.core.common.mail.dto.CompanyAlertDto

data class CompanySnapshot(
    val nameKr: String,
    val nameEn: String,
    val logo: String,
    val blogUrl: String,
) {
    companion object {
        fun from(company: CompanyAlertDto): CompanySnapshot {
            return CompanySnapshot(
                nameKr = company.nameKr,
                nameEn = company.nameEn,
                logo = company.logo,
                blogUrl = company.blogUrl
            )
        }
    }
}
