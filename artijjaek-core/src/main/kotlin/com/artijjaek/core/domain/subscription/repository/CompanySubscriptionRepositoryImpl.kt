package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.company.entity.QCompany.company
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.subscription.dto.TopSubscribedCompanyCount
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.entity.QCompanySubscription.companySubscription
import com.querydsl.jpa.impl.JPAQueryFactory


class CompanySubscriptionRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CompanySubscriptionRepositoryCustom {

    override fun findAllByMemberFetchCompany(member: Member): List<CompanySubscription> {
        return jpaQueryFactory.selectFrom(companySubscription)
            .leftJoin(companySubscription.company, company).fetchJoin()
            .where(companySubscription.member.id.eq(member.id))
            .fetch()
    }

    override fun findTopSubscribedCompaniesWithTies(limit: Int): List<TopSubscribedCompanyCount> {
        if (limit <= 0) {
            return emptyList()
        }

        val subscriptionCount = companySubscription.id.count()
        val topCounts = jpaQueryFactory
            .select(subscriptionCount)
            .from(companySubscription)
            .groupBy(companySubscription.company.id)
            .orderBy(subscriptionCount.desc())
            .limit(limit.toLong())
            .fetch()
            .filterNotNull()

        if (topCounts.isEmpty()) {
            return emptyList()
        }

        val thresholdCount = topCounts.last()

        return jpaQueryFactory
            .select(company.id, company.nameKr, subscriptionCount)
            .from(companySubscription)
            .join(companySubscription.company, company)
            .groupBy(company.id, company.nameKr)
            .having(subscriptionCount.goe(thresholdCount))
            .orderBy(subscriptionCount.desc(), company.nameKr.asc(), company.id.asc())
            .fetch()
            .mapNotNull { tuple ->
                val companyId = tuple.get(company.id)
                val companyNameKr = tuple.get(company.nameKr)
                val count = tuple.get(subscriptionCount)

                if (companyId == null || companyNameKr == null || count == null) {
                    null
                } else {
                    TopSubscribedCompanyCount(
                        companyId = companyId,
                        companyNameKr = companyNameKr,
                        subscriberCount = count
                    )
                }
            }
    }
}
