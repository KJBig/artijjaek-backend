package com.artijjaek.api.service

import com.artijjaek.api.dto.request.SubscriptionChangeRequest
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberDomainService
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubscriptionService(
    private val memberDomainService: MemberDomainService,
    private val companyDomainService: CompanyDomainService,
    private val companySubscriptionDomainService: CompanySubscriptionDomainService,
) {

    @Transactional
    fun changeSubscription(request: SubscriptionChangeRequest) {
        val member = memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
            ?: throw IllegalStateException("Member Not Found.")

        if (member.uuidToken != request.token) {
            throw IllegalArgumentException("Token is not matched.")
        }

        companySubscriptionDomainService.deleteAllByMemberId(member.id!!)

        val companies: List<Company> = companyDomainService.findByIdsOrAll(request.companyIds)

        val companySubscriptions = companies.map { CompanySubscription(member = member, company = it) }
        companySubscriptionDomainService.saveAll(companySubscriptions)
    }

    @Transactional
    fun chancelSubscription(email: String, token: String) {
        val member = memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE)
            ?: throw IllegalStateException("Member Not Found.")

        if (member.uuidToken != token) {
            throw IllegalArgumentException("Token is not matched.")
        }

        member.changeMemberStatus(MemberStatus.DELETED)
    }

}