package com.artijjaek.admin.service

import com.artijjaek.admin.dto.response.MemberOptionCompanyResponse
import com.artijjaek.admin.dto.response.TopSubscribedCompanyResponse
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
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
