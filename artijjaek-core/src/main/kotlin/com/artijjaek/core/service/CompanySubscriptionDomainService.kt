package com.artijjaek.core.service

import com.artijjaek.core.domain.CompanySubscription
import com.artijjaek.core.domain.Member
import com.artijjaek.core.repository.CompanySubscriptionRepository
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