package com.artijjaek.core.domain.subscription.service

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.repository.CategorySubscriptionRepository
import org.springframework.stereotype.Service

@Service
class CategorySubscriptionDomainService(
    private val categorySubscriptionRepository: CategorySubscriptionRepository,
) {

    fun saveAll(categorySubscriptions: List<CategorySubscription>) {
        categorySubscriptionRepository.saveAll(categorySubscriptions)
    }

    fun deleteAllByMemberId(memberId: Long) {
        categorySubscriptionRepository.deleteAllByMemberId(memberId)
    }

    fun findAllByMember(member: Member): List<CategorySubscription> {
        return categorySubscriptionRepository.findAllByMember(member)
    }

}