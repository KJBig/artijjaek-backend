package com.artijjaek.core.domain.subscription.repository

import com.artijjaek.core.domain.company.entity.QCompany.company
import com.artijjaek.core.domain.member.entity.Member
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

}