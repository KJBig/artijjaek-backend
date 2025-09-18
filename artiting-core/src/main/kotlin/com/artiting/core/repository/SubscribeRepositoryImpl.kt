package com.artiting.core.repository

import com.artiting.core.domain.Member
import com.artiting.core.domain.QCompany.company
import com.artiting.core.domain.QSubscribe.subscribe
import com.artiting.core.domain.Subscribe
import com.querydsl.jpa.impl.JPAQueryFactory


class SubscribeRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : SubscribeRepositoryCustom {

    override fun findAllByMember(member: Member): List<Subscribe> {
        return jpaQueryFactory.selectFrom(subscribe)
            .leftJoin(subscribe.company, company).fetchJoin()
            .where(subscribe.member.eq(member))
            .fetch()
    }

}