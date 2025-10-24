package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import org.springframework.data.jpa.repository.JpaRepository

interface CategorySubscriptionRepository : JpaRepository<CategorySubscription, Long> {
    fun deleteAllByMemberId(memberId: Long?)
}