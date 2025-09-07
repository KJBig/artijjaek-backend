package com.noati.core.repository

import com.noati.core.domain.Member
import com.noati.core.domain.QCompany.company
import com.noati.core.domain.QSubscribe.subscribe
import com.noati.core.domain.Subscribe
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