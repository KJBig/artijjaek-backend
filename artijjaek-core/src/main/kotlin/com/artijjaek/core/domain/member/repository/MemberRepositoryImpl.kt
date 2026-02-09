package com.artijjaek.core.domain.member.repository

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.entity.QMember.member
import com.artijjaek.core.domain.member.enums.MemberSortBy
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.support.PageableExecutionUtils

class MemberRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : MemberRepositoryCustom {

    override fun findWithCondition(
        pageable: Pageable,
        memberStatus: MemberStatus?,
        nicknameKeyword: String?,
        emailKeyword: String?,
        sortBy: MemberSortBy,
        sortDirection: Sort.Direction,
    ): Page<Member> {
        val orderSpecifiers = when (sortBy) {
            MemberSortBy.CREATED_AT -> arrayOf(createdAtOrder(sortDirection), idOrder(sortDirection))
            MemberSortBy.NICKNAME -> arrayOf(nicknameOrder(sortDirection), idOrder(sortDirection))
            MemberSortBy.EMAIL -> arrayOf(emailOrder(sortDirection), idOrder(sortDirection))
            MemberSortBy.STATUS -> arrayOf(statusOrder(sortDirection), idOrder(sortDirection))
        }

        val content = jpaQueryFactory
            .selectFrom(member)
            .where(
                memberStatusEq(memberStatus),
                keywordCondition(nicknameKeyword, emailKeyword),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*orderSpecifiers)
            .fetch()

        val countQuery = jpaQueryFactory
            .select(member.id.count())
            .from(member)
            .where(
                memberStatusEq(memberStatus),
                keywordCondition(nicknameKeyword, emailKeyword),
            )

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    override fun countByMemberStatus(memberStatus: MemberStatus?): Long {
        return jpaQueryFactory
            .select(member.id.count())
            .from(member)
            .where(memberStatusEq(memberStatus))
            .fetchOne() ?: 0L
    }

    private fun memberStatusEq(memberStatus: MemberStatus?): BooleanExpression? {
        return memberStatus?.let { member.memberStatus.eq(it) }
    }

    private fun nicknameContains(keyword: String?): BooleanExpression? {
        return keyword?.takeIf { it.isNotBlank() }?.let { member.nickname.contains(it) }
    }

    private fun emailContains(keyword: String?): BooleanExpression? {
        return keyword?.takeIf { it.isNotBlank() }?.let { member.email.contains(it) }
    }

    private fun keywordCondition(nicknameKeyword: String?, emailKeyword: String?): BooleanExpression? {
        val nicknameCondition = nicknameContains(nicknameKeyword)
        val emailCondition = emailContains(emailKeyword)

        return when {
            nicknameCondition != null && emailCondition != null -> nicknameCondition.or(emailCondition)
            nicknameCondition != null -> nicknameCondition
            else -> emailCondition
        }
    }

    private fun createdAtOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        return if (sortDirection.isAscending) {
            member.createdAt.asc()
        } else {
            member.createdAt.desc()
        }
    }

    private fun nicknameOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        return if (sortDirection.isAscending) {
            member.nickname.asc()
        } else {
            member.nickname.desc()
        }
    }

    private fun emailOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        return if (sortDirection.isAscending) {
            member.email.asc()
        } else {
            member.email.desc()
        }
    }

    private fun statusOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        return if (sortDirection.isAscending) {
            member.memberStatus.asc()
        } else {
            member.memberStatus.desc()
        }
    }

    private fun idOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        val order = if (sortDirection.isAscending) Order.ASC else Order.DESC
        return OrderSpecifier(order, member.id)
    }
}
