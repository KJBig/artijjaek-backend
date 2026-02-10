package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.subscription.entity.CategorySubscription

interface CategorySubscriptionRepositoryCustom {
    fun findAllByMemberFetchCategory(member: Member): List<CategorySubscription>
}