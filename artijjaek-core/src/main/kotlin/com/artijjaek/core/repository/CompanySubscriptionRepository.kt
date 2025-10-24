package com.artijjaek.core.repository

import com.artijjaek.core.domain.CompanySubscription
import org.springframework.data.jpa.repository.JpaRepository

interface CompanySubscriptionRepository : JpaRepository<CompanySubscription, Long>,
    CompanySubscriptionRepositoryCustom {
    fun deleteAllByMemberId(memberId: Long?)
}