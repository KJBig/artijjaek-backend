package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.subscription.entity.CompanySubscription

interface CompanySubscriptionRepositoryCustom {
    fun findAllByMemberFetchCompany(member: Member): List<CompanySubscription>
}