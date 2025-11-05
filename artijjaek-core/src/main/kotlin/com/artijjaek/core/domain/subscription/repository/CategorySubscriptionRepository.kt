package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import org.springframework.data.jpa.repository.JpaRepository

interface CategorySubscriptionRepository : JpaRepository<CategorySubscription, Long>,
    CategorySubscriptionRepositoryCustom {
    fun deleteAllByMemberId(memberId: Long?)
}