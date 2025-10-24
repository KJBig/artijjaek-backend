package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import org.springframework.data.jpa.repository.JpaRepository

interface CompanySubscriptionRepository : JpaRepository<CompanySubscription, Long>,
    CompanySubscriptionRepositoryCustom {
    fun deleteAllByMemberId(memberId: Long?)
}