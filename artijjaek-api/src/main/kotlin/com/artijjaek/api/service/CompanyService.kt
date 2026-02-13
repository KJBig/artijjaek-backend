package com.artijjaek.api.service

import com.artijjaek.api.dto.common.PageResponse
import com.artijjaek.api.dto.response.CompanyCountResponse
import com.artijjaek.api.dto.response.CompanySimpleDataResponse
import com.artijjaek.core.domain.company.enums.CompanySortOption
import com.artijjaek.core.domain.company.service.CompanyDomainService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CompanyService(
    private val companyDomainService: CompanyDomainService,
) {

    @Transactional(readOnly = true)
    fun searchCompanyList(sortOption: CompanySortOption, pageable: Pageable): PageResponse<CompanySimpleDataResponse> {
        val companyPage = companyDomainService.findWithPageableOrderBySortOption(sortOption, pageable)
        val content = companyPage.content.stream()
            .map { CompanySimpleDataResponse.from(it) }
            .toList()

        return PageResponse(companyPage.pageable.pageNumber, companyPage.hasNext(), content)
    }

    @Transactional(readOnly = true)
    fun getCompanyCount(): CompanyCountResponse {
        val companyCount = companyDomainService.countCompanies()
        return CompanyCountResponse(companyCount)
    }
}
