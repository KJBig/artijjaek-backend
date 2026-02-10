package com.artijjaek.admin.service

import com.artijjaek.admin.dto.response.MemberOptionCompanyResponse
import com.artijjaek.core.domain.company.service.CompanyDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminCompanyService(
    private val companyDomainService: CompanyDomainService,
) {

    @Transactional(readOnly = true)
    fun getMemberCompanyOptions(): List<MemberOptionCompanyResponse> {
        return companyDomainService.findAll().map {
            MemberOptionCompanyResponse(
                companyId = it.id!!,
                companyNameKr = it.nameKr,
                companyNameEn = it.nameEn,
                logo = it.logo
            )
        }
    }
}
