package com.artijjaek.core.domain.mail.repository

import com.artijjaek.core.domain.mail.entity.QEmailOutbox.emailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import java.time.LocalDateTime

class EmailOutboxRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : EmailOutboxRepositoryCustom {

    override fun findDueIds(now: LocalDateTime, limit: Int): List<Long> {
        return jpaQueryFactory
            .select(emailOutbox.id)
            .from(emailOutbox)
            .where(dueCondition(now))
            .orderBy(emailOutbox.id.asc())
            .limit(limit.toLong())
            .fetch()
            .filterNotNull()
    }

    override fun existsDue(now: LocalDateTime): Boolean {
        val existsValue = jpaQueryFactory
            .selectOne()
            .from(emailOutbox)
            .where(dueCondition(now))
            .fetchFirst()

        return existsValue != null
    }

    override fun findEarliestRetryAt(): LocalDateTime? {
        return jpaQueryFactory
            .select(emailOutbox.nextRetryAt.min())
            .from(emailOutbox)
            .where(
                emailOutbox.status.eq(EmailOutboxStatus.FAIL),
                emailOutbox.nextRetryAt.isNotNull
            )
            .fetchOne()
    }

    override fun claimForSending(id: Long, now: LocalDateTime): Boolean {
        val updated = jpaQueryFactory
            .update(emailOutbox)
            .set(emailOutbox.status, EmailOutboxStatus.SENDING)
            .where(
                emailOutbox.id.eq(id),
                dueCondition(now)
            )
            .execute()

        return updated > 0
    }

    private fun dueCondition(now: LocalDateTime): BooleanExpression {
        return emailOutbox.status.eq(EmailOutboxStatus.PENDING)
            .or(
                emailOutbox.status.eq(EmailOutboxStatus.FAIL)
                    .and(emailOutbox.nextRetryAt.loe(now))
            )
    }
}
