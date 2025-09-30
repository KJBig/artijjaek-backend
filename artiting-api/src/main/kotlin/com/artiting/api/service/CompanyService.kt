package com.artiting.api.service

import com.artiting.api.dto.common.PageResponse
import com.artiting.api.dto.response.CompanySimpleDataResponse
import com.artiting.core.service.CompanyDomainService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CompanyService(
    private val companyDomainService: CompanyDomainService,
) {

    @Transactional(readOnly = true)
    fun searchCompanyList(pageable: Pageable): PageResponse<CompanySimpleDataResponse> {
        val companyPage = companyDomainService.findWithPageable(pageable)
        val content = companyPage.content.stream().map { CompanySimpleDataResponse.from(it) }.toList()
        return PageResponse(companyPage.pageable.pageNumber, companyPage.hasNext(), content)
    }
}