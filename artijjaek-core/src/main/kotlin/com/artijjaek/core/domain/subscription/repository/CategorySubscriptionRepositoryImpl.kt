package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.category.entity.QCategory.category
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.subscription.dto.TopSubscribedCategoryCount
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.entity.QCategorySubscription.categorySubscription
import com.querydsl.jpa.impl.JPAQueryFactory


class CategorySubscriptionRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CategorySubscriptionRepositoryCustom {

    override fun findAllByMemberFetchCategory(member: Member): List<CategorySubscription> {
        return jpaQueryFactory.selectFrom(categorySubscription)
            .leftJoin(categorySubscription.category, category).fetchJoin()
            .where(categorySubscription.member.eq(member))
            .fetch()
    }

    override fun findTopSubscribedCategoriesWithTies(limit: Int): List<TopSubscribedCategoryCount> {
        if (limit <= 0) {
            return emptyList()
        }

        val subscriptionCount = categorySubscription.id.count()
        val topCounts = jpaQueryFactory
            .select(subscriptionCount)
            .from(categorySubscription)
            .groupBy(categorySubscription.category.id)
            .orderBy(subscriptionCount.desc())
            .limit(limit.toLong())
            .fetch()
            .filterNotNull()

        if (topCounts.isEmpty()) {
            return emptyList()
        }

        val thresholdCount = topCounts.last()

        return jpaQueryFactory
            .select(category.id, category.name, subscriptionCount)
            .from(categorySubscription)
            .join(categorySubscription.category, category)
            .groupBy(category.id, category.name)
            .having(subscriptionCount.goe(thresholdCount))
            .orderBy(subscriptionCount.desc(), category.name.asc(), category.id.asc())
            .fetch()
            .mapNotNull { tuple ->
                val categoryId = tuple.get(category.id)
                val categoryName = tuple.get(category.name)
                val count = tuple.get(subscriptionCount)

                if (categoryId == null || categoryName == null || count == null) {
                    null
                } else {
                    TopSubscribedCategoryCount(
                        categoryId = categoryId,
                        categoryName = categoryName,
                        subscriberCount = count
                    )
                }
            }
    }
}
