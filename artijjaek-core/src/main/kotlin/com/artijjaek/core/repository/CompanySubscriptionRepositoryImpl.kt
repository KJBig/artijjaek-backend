package com.artijjaek.core.repository

import com.artijjaek.core.domain.CompanySubscription
import com.artijjaek.core.domain.Member
import com.artijjaek.core.domain.QCompany.company
import com.artijjaek.core.domain.QSubscribe.subscribe
import com.querydsl.jpa.impl.JPAQueryFactory


class CompanySubscriptionRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CompanySubscriptionRepositoryCustom {

    override fun findAllByMember(member: Member): List<CompanySubscription> {
        return jpaQueryFactory.selectFrom(subscribe)
            .leftJoin(subscribe.company, company).fetchJoin()
            .where(subscribe.member.eq(member))
            .fetch()
    }

}