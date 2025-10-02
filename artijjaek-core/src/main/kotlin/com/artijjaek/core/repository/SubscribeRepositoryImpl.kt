package com.artijjaek.core.repository

import com.artijjaek.core.domain.Member
import com.artijjaek.core.domain.QCompany.company
import com.artijjaek.core.domain.QSubscribe.subscribe
import com.artijjaek.core.domain.Subscribe
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