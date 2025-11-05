package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.category.entity.QCategory.category
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.entity.QCategorySubscription.categorySubscription
import com.querydsl.jpa.impl.JPAQueryFactory


class CategorySubscriptionRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CategorySubscriptionRepositoryCustom {

    override fun findAllByMember(member: Member): List<CategorySubscription> {
        return jpaQueryFactory.selectFrom(categorySubscription)
            .leftJoin(categorySubscription.category, category).fetchJoin()
            .where(categorySubscription.member.eq(member))
            .fetch()
    }

}