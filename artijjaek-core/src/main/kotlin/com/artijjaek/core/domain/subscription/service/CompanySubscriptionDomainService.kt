package com.artijjaek.core.domain.subscription.service

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.repository.CompanySubscriptionRepository
import org.springframework.stereotype.Service

@Service
class CompanySubscriptionDomainService(
    val companySubscriptionRepository: CompanySubscriptionRepository,
) {
    fun findAllByMember(member: Member): List<CompanySubscription> {
        return companySubscriptionRepository.findAllByMember(member)
    }

    fun saveAll(companySubscriptions: List<CompanySubscription>) {
        companySubscriptionRepository.saveAll(companySubscriptions)
    }

    fun deleteAllByMemberId(memberId: Long) {
        companySubscriptionRepository.deleteAllByMemberId(memberId)
    }
}