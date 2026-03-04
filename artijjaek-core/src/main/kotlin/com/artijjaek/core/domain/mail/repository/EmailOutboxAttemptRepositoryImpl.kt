package com.artijjaek.core.domain.mail.repository

import com.artijjaek.core.domain.mail.dto.DailyEmailSendAttemptCount
import com.artijjaek.core.domain.mail.entity.EmailOutboxAttempt
import com.artijjaek.core.domain.mail.entity.QEmailOutbox.emailOutbox
import com.artijjaek.core.domain.mail.entity.QEmailOutboxAttempt.emailOutboxAttempt
import com.artijjaek.core.domain.mail.enums.EmailOutboxAttemptResult
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import java.sql.Date
import java.time.LocalDateTime

class EmailOutboxAttemptRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : EmailOutboxAttemptRepositoryCustom {
    override fun search(
        pageable: Pageable,
        status: EmailOutboxAttemptResult?,
        requestedBy: EmailOutboxRequestedBy?,
        occurredAtFrom: LocalDateTime?,
        occurredAtTo: LocalDateTime?,
    ): Page<EmailOutboxAttempt> {
        val content = jpaQueryFactory
            .selectFrom(emailOutboxAttempt)
            .join(emailOutboxAttempt.emailOutbox, emailOutbox).fetchJoin()
            .where(
                statusEq(status),
                requestedByEq(requestedBy),
                occurredAtGoe(occurredAtFrom),
                occurredAtLt(occurredAtTo),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(emailOutboxAttempt.occurredAt.desc(), emailOutboxAttempt.id.desc())
            .fetch()

        val countQuery = jpaQueryFactory
            .select(emailOutboxAttempt.id.count())
            .from(emailOutboxAttempt)
            .where(
                statusEq(status),
                requestedByEq(requestedBy),
                occurredAtGoe(occurredAtFrom),
                occurredAtLt(occurredAtTo),
            )

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }


    override fun countDailySuccessAttempts(
        startDateTime: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
        requestedBy: EmailOutboxRequestedBy?,
    ): List<DailyEmailSendAttemptCount> {
        return countDailyAttempts(
            startDateTime = startDateTime,
            endDateTimeExclusive = endDateTimeExclusive,
            requestedBy = requestedBy,
            result = EmailOutboxAttemptResult.SUCCESS
        )
    }

    override fun countDailyFailureAttempts(
        startDateTime: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
        requestedBy: EmailOutboxRequestedBy?,
    ): List<DailyEmailSendAttemptCount> {
        return countDailyAttempts(
            startDateTime = startDateTime,
            endDateTimeExclusive = endDateTimeExclusive,
            requestedBy = requestedBy,
            result = EmailOutboxAttemptResult.FAIL
        )
    }

    private fun countDailyAttempts(
        startDateTime: LocalDateTime,
        endDateTimeExclusive: LocalDateTime,
        requestedBy: EmailOutboxRequestedBy?,
        result: EmailOutboxAttemptResult,
    ): List<DailyEmailSendAttemptCount> {
        val occurredDate = Expressions.dateTemplate(Date::class.java, "date({0})", emailOutboxAttempt.occurredAt)
        val attemptCount = emailOutboxAttempt.id.count()

        return jpaQueryFactory
            .select(occurredDate, attemptCount)
            .from(emailOutboxAttempt)
            .where(
                emailOutboxAttempt.occurredAt.goe(startDateTime),
                emailOutboxAttempt.occurredAt.lt(endDateTimeExclusive),
                emailOutboxAttempt.result.eq(result),
                requestedByEq(requestedBy)
            )
            .groupBy(occurredDate)
            .orderBy(occurredDate.asc())
            .fetch()
            .mapNotNull { tuple ->
                val sqlDate = tuple.get(occurredDate)
                val count = tuple.get(attemptCount)

                if (sqlDate == null || count == null) {
                    null
                } else {
                    DailyEmailSendAttemptCount(date = sqlDate.toLocalDate(), count = count)
                }
            }
    }

    private fun requestedByEq(requestedBy: EmailOutboxRequestedBy?): BooleanExpression? {
        return requestedBy?.let { emailOutboxAttempt.requestedBy.eq(it) }
    }

    private fun statusEq(status: EmailOutboxAttemptResult?): BooleanExpression? {
        return status?.let { emailOutboxAttempt.result.eq(it) }
    }

    private fun occurredAtGoe(from: LocalDateTime?): BooleanExpression? {
        return from?.let { emailOutboxAttempt.occurredAt.goe(it) }
    }

    private fun occurredAtLt(to: LocalDateTime?): BooleanExpression? {
        return to?.let { emailOutboxAttempt.occurredAt.lt(it) }
    }
}
