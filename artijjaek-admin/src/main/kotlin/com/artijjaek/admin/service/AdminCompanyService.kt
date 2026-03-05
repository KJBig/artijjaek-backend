package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostCompanyRequest
import com.artijjaek.admin.dto.request.PutCompanyRequest
import com.artijjaek.admin.dto.response.CompanyListPageResponse
import com.artijjaek.admin.dto.response.CompanySimpleResponse
import com.artijjaek.admin.dto.response.MemberOptionCompanyResponse
import com.artijjaek.admin.dto.response.PostCompanyResponse
import com.artijjaek.admin.dto.response.TopSubscribedCompanyResponse
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.COMPANY_NOT_FOUND_ERROR
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminCompanyService(
    private val companyDomainService: CompanyDomainService,
    private val companySubscriptionDomainService: CompanySubscriptionDomainService,
) {
    companion object {
        private const val TOP_SUBSCRIBED_LIMIT = 5
    }

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

    @Transactional(readOnly = true)
    fun getTopSubscribedCompanies(): List<TopSubscribedCompanyResponse> {
        val topCompanies = companySubscriptionDomainService.findTopSubscribedCompaniesWithTies(TOP_SUBSCRIBED_LIMIT)
        return denseRank(topCompanies.map { it.subscriberCount }).mapIndexed { index, rank ->
            TopSubscribedCompanyResponse(
                rank = rank,
                companyId = topCompanies[index].companyId,
                companyNameKr = topCompanies[index].companyNameKr,
                subscriberCount = topCompanies[index].subscriberCount
            )
        }
    }

    @Transactional(readOnly = true)
    fun searchCompanies(pageable: Pageable, keyword: String?): CompanyListPageResponse {
        val page = companyDomainService.findWithCondition(
            pageable = PageRequest.of(pageable.pageNumber, pageable.pageSize),
            keyword = keyword
        )

        return CompanyListPageResponse(
            pageNumber = page.number,
            totalCount = page.totalElements,
            hasNext = page.hasNext(),
            content = page.content.map {
                CompanySimpleResponse(
                    companyId = it.id!!,
                    nameKr = it.nameKr,
                    nameEn = it.nameEn,
                    logo = it.logo,
                    baseUrl = it.baseUrl,
                    blogUrl = it.blogUrl,
                    crawlUrl = it.crawlUrl,
                    crawlAvailability = it.crawlAvailability,
                    crawlPattern = it.crawlPattern,
                    crawlOrder = it.crawlOrder,
                    createdAt = it.createdAt!!
                )
            }
        )
    }

    @Transactional
    fun createCompany(request: PostCompanyRequest): PostCompanyResponse {
        val company = Company(
            nameKr = request.nameKr,
            nameEn = request.nameEn,
            logo = request.logo,
            baseUrl = request.baseUrl,
            blogUrl = request.blogUrl,
            crawlUrl = request.crawlUrl,
            crawlAvailability = request.crawlAvailability,
            crawlPattern = request.crawlPattern,
            crawlOrder = request.crawlOrder
        )
        companyDomainService.save(company)

        return PostCompanyResponse(companyId = company.id!!)
    }

    @Transactional
    fun updateCompany(companyId: Long, request: PutCompanyRequest) {
        val company = companyDomainService.findById(companyId)
            ?: throw ApplicationException(COMPANY_NOT_FOUND_ERROR)

        company.nameKr = request.nameKr
        company.nameEn = request.nameEn
        company.logo = request.logo
        company.baseUrl = request.baseUrl
        company.blogUrl = request.blogUrl
        company.crawlUrl = request.crawlUrl
        company.crawlAvailability = request.crawlAvailability
        company.crawlPattern = request.crawlPattern
        company.crawlOrder = request.crawlOrder

        companyDomainService.save(company)
    }

    private fun denseRank(counts: List<Long>): List<Int> {
        var previousCount: Long? = null
        var rank = 0

        return counts.mapIndexed { index, count ->
            if (previousCount == null || previousCount != count) {
                rank = index + 1
                previousCount = count
            }
            rank
        }
    }
}
