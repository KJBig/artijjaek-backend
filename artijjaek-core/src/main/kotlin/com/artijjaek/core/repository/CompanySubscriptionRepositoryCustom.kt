package com.artijjaek.core.repository

import com.artijjaek.core.domain.CompanySubscription
import com.artijjaek.core.domain.Member

interface CompanySubscriptionRepositoryCustom {
    fun findAllByMember(member: Member): List<CompanySubscription>
}