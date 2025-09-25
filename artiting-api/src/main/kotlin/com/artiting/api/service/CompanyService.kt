package com.artiting.api.service

import com.artiting.api.dto.response.CompanySimpleDataResponse
import com.artiting.core.service.CompanyDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CompanyService(
    private val companyDomainService: CompanyDomainService,
) {

    @Transactional(readOnly = true)
    fun searchCompanyList(page: Int?, size: Int?): List<CompanySimpleDataResponse> {
        return companyDomainService.findWithPageable(page, size).stream()
            .map { company ->
                CompanySimpleDataResponse(
                    companyId = company.id!!,
                    companyNameKr = company.nameKr,
                    companyNameEn = company.nameEn,
                    companyImageUrl = company.logo
                )
            }.toList()
    }
}