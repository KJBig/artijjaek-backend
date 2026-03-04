package com.artijjaek.core.domain.mail.repository

import com.artijjaek.core.domain.mail.entity.QEmailOutbox.emailOutbox
import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
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

    override fun markEnqueued(id: Long, now: LocalDateTime): Boolean {
        val updated = jpaQueryFactory
            .update(emailOutbox)
            .set(emailOutbox.status, EmailOutboxStatus.ENQUEUED)
            .where(
                emailOutbox.id.eq(id),
                dueCondition(now)
            )
            .execute()

        return updated > 0
    }

    override fun search(
        pageable: Pageable,
        status: EmailOutboxStatus?,
        mailType: EmailOutboxType?,
        requestedBy: EmailOutboxRequestedBy?,
        recipientEmail: String?,
        requestedAtFrom: LocalDateTime?,
        requestedAtTo: LocalDateTime?,
    ): Page<EmailOutbox> {
        val orderSpecifiers = resolveOrderSpecifiers(pageable)

        val content = jpaQueryFactory
            .selectFrom(emailOutbox)
            .where(
                statusEq(status),
                mailTypeEq(mailType),
                requestedByEq(requestedBy),
                recipientContains(recipientEmail),
                requestedAtGoe(requestedAtFrom),
                requestedAtLt(requestedAtTo),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*orderSpecifiers)
            .fetch()

        val countQuery = jpaQueryFactory
            .select(emailOutbox.id.count())
            .from(emailOutbox)
            .where(
                statusEq(status),
                mailTypeEq(mailType),
                requestedByEq(requestedBy),
                recipientContains(recipientEmail),
                requestedAtGoe(requestedAtFrom),
                requestedAtLt(requestedAtTo),
            )

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    override fun findOldestDueRequestedAt(now: LocalDateTime): LocalDateTime? {
        val dueBaseAt = CaseBuilder()
            .`when`(emailOutbox.status.eq(EmailOutboxStatus.FAIL))
            .then(emailOutbox.nextRetryAt)
            .otherwise(emailOutbox.requestedAt)

        return jpaQueryFactory
            .select(dueBaseAt.min())
            .from(emailOutbox)
            .where(dueCondition(now))
            .fetchOne()
    }

    private fun dueCondition(now: LocalDateTime): BooleanExpression {
        return emailOutbox.status.eq(EmailOutboxStatus.PENDING)
            .or(emailOutbox.status.eq(EmailOutboxStatus.ENQUEUED))
            .or(
                emailOutbox.status.eq(EmailOutboxStatus.FAIL)
                    .and(emailOutbox.nextRetryAt.loe(now))
            )
    }

    private fun statusEq(status: EmailOutboxStatus?): BooleanExpression? {
        return status?.let { emailOutbox.status.eq(it) }
    }

    private fun mailTypeEq(mailType: EmailOutboxType?): BooleanExpression? {
        return mailType?.let { emailOutbox.mailType.eq(it) }
    }

    private fun requestedByEq(requestedBy: EmailOutboxRequestedBy?): BooleanExpression? {
        return requestedBy?.let { emailOutbox.requestedBy.eq(it) }
    }

    private fun recipientContains(recipientEmail: String?): BooleanExpression? {
        return recipientEmail?.takeIf { it.isNotBlank() }?.let { emailOutbox.recipientEmail.contains(it.trim()) }
    }

    private fun requestedAtGoe(from: LocalDateTime?): BooleanExpression? {
        return from?.let { emailOutbox.requestedAt.goe(it) }
    }

    private fun requestedAtLt(to: LocalDateTime?): BooleanExpression? {
        return to?.let { emailOutbox.requestedAt.lt(it) }
    }

    private fun resolveOrderSpecifiers(pageable: Pageable): Array<OrderSpecifier<*>> {
        if (pageable.sort.isUnsorted) {
            return arrayOf(emailOutbox.requestedAt.desc(), emailOutbox.id.desc())
        }

        val mapped = pageable.sort.mapNotNull { order ->
            when (order.property) {
                "requestedAt" -> toOrderSpecifier(emailOutbox.requestedAt, order.isAscending)
                "createdAt" -> toOrderSpecifier(emailOutbox.createdAt, order.isAscending)
                "updatedAt" -> toOrderSpecifier(emailOutbox.updatedAt, order.isAscending)
                "id" -> toOrderSpecifier(emailOutbox.id, order.isAscending)
                else -> null
            }
        }.toList()

        return if (mapped.isEmpty()) {
            arrayOf(emailOutbox.requestedAt.desc(), emailOutbox.id.desc())
        } else {
            mapped.toTypedArray()
        }
    }

    private fun <T : Comparable<*>> toOrderSpecifier(path: com.querydsl.core.types.dsl.ComparableExpressionBase<T>, ascending: Boolean): OrderSpecifier<T> {
        return if (ascending) {
            OrderSpecifier(Order.ASC, path)
        } else {
            OrderSpecifier(Order.DESC, path)
        }
    }
}
