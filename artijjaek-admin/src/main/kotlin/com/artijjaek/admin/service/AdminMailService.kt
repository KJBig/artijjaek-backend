package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostArticleMailRequest
import com.artijjaek.admin.dto.request.PostNoticeMailRequest
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.admin.dto.response.MailDailyFailedCountResponse
import com.artijjaek.admin.dto.response.MailDailySentCountResponse
import com.artijjaek.admin.dto.response.MailOutboxPageResponse
import com.artijjaek.admin.dto.response.MailOutboxSimpleResponse
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.*
import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.artijjaek.core.domain.mail.queue.publisher.MailQueuePublisher
import com.artijjaek.core.domain.mail.queue.trigger.MailDispatchTrigger
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.core.domain.member.service.MemberDomainService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class AdminMailService(
    private val memberDomainService: MemberDomainService,
    private val articleDomainService: ArticleDomainService,
    private val mailQueuePublisher: MailQueuePublisher,
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val mailDispatchTrigger: MailDispatchTrigger,
) {

    @Transactional
    fun sendWelcomeMail(request: PostWelcomeMailRequest) {
        request.memberIds.distinct().forEach { memberId ->
            val member = memberDomainService.findById(memberId)
                ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

            if (member.email.isNullOrBlank()) {
                throw ApplicationException(MEMBER_EMAIL_NOT_FOUND_ERROR)
            }

            mailQueuePublisher.enqueueWelcomeMail(MemberAlertDto.from(member), EmailOutboxRequestedBy.ADMIN_API)
        }
    }

    @Transactional
    fun sendArticleMail(request: PostArticleMailRequest) {
        val articleIds = request.articleIds.distinct()
        val articles = articleDomainService.findAllByIdsWithCompany(articleIds)

        if (articles.size != articleIds.size) {
            throw ApplicationException(ARTICLE_NOT_FOUND_ERROR)
        }

        val articleAlertDtos = articles
            .sortedByDescending { it.createdAt }
            .map { ArticleAlertDto.from(it) }

        request.memberIds.distinct().forEach { memberId ->
            val member = memberDomainService.findById(memberId)
                ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

            if (member.email.isNullOrBlank()) {
                throw ApplicationException(MEMBER_EMAIL_NOT_FOUND_ERROR)
            }

            mailQueuePublisher.enqueueArticleMail(
                memberData = MemberAlertDto.from(member),
                articleDatas = articleAlertDtos,
                requestedBy = EmailOutboxRequestedBy.ADMIN_API
            )
        }
    }

    @Transactional
    fun sendNoticeMail(request: PostNoticeMailRequest) {
        val title = request.title.trim()
        val content = request.content.trim()

        request.memberIds.distinct().forEach { memberId ->
            val member = memberDomainService.findById(memberId)
                ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

            if (member.email.isNullOrBlank()) {
                throw ApplicationException(MEMBER_EMAIL_NOT_FOUND_ERROR)
            }

            mailQueuePublisher.enqueueNoticeMail(
                memberData = MemberAlertDto.from(member),
                title = title,
                content = content,
                requestedBy = EmailOutboxRequestedBy.ADMIN_API
            )
        }
    }

    @Transactional(readOnly = true)
    fun searchOutboxes(
        pageable: Pageable,
        status: EmailOutboxStatus?,
        mailType: EmailOutboxType?,
        requestedBy: EmailOutboxRequestedBy?,
        recipientEmail: String?,
        requestedAtFrom: LocalDateTime?,
        requestedAtTo: LocalDateTime?,
    ): MailOutboxPageResponse {
        val sortedPageable = if (pageable.sort.isUnsorted) {
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Sort.Direction.DESC, "requestedAt"))
        } else {
            pageable
        }

        val page = emailOutboxDomainService.search(
            pageable = sortedPageable,
            status = status,
            mailType = mailType,
            requestedBy = requestedBy,
            recipientEmail = recipientEmail,
            requestedAtFrom = requestedAtFrom,
            requestedAtTo = requestedAtTo
        )

        return MailOutboxPageResponse(
            pageNumber = page.number,
            totalCount = page.totalElements,
            hasNext = page.hasNext(),
            content = page.content.map { MailOutboxSimpleResponse.from(it) }
        )
    }

    @Transactional
    fun retryOutbox(outboxId: Long, resetAttempts: Boolean, retriedByAdminId: Long) {
        val outbox = emailOutboxDomainService.findById(outboxId)
            ?: throw ApplicationException(MAIL_OUTBOX_NOT_FOUND_ERROR)

        if (outbox.status != EmailOutboxStatus.FAIL && outbox.status != EmailOutboxStatus.DEAD) {
            throw ApplicationException(MAIL_OUTBOX_RETRY_NOT_ALLOWED_ERROR)
        }

        if (resetAttempts) {
            outbox.attemptCount = 0
        }

        outbox.status = EmailOutboxStatus.PENDING
        outbox.nextRetryAt = null
        outbox.lastError = null
        outbox.manualRetryCount = outbox.manualRetryCount + 1
        outbox.lastRetriedByAdminId = retriedByAdminId
        outbox.lastRetriedAt = LocalDateTime.now()
        val saved = emailOutboxDomainService.save(outbox)
        mailDispatchTrigger.dispatchOutbox(saved.id!!)
    }

    @Transactional(readOnly = true)
    fun getDailySentCounts(
        startDate: LocalDate,
        endDate: LocalDate,
        requestedBy: EmailOutboxRequestedBy?,
    ): List<MailDailySentCountResponse> {
        validateDateRange(startDate, endDate)

        val dailyCountMap = emailOutboxDomainService.countDailySuccessAttempts(
            startDateTime = startDate.atStartOfDay(),
            endDateTimeExclusive = endDate.plusDays(1).atStartOfDay(),
            requestedBy = requestedBy
        ).associate { it.date to it.count }

        return startDate.datesUntil(endDate.plusDays(1))
            .map { date ->
                MailDailySentCountResponse(
                    date = date,
                    sentCount = dailyCountMap[date] ?: 0L
                )
            }
            .toList()
    }

    @Transactional(readOnly = true)
    fun getDailyFailedCounts(
        startDate: LocalDate,
        endDate: LocalDate,
        requestedBy: EmailOutboxRequestedBy?,
    ): List<MailDailyFailedCountResponse> {
        validateDateRange(startDate, endDate)

        val dailyCountMap = emailOutboxDomainService.countDailyFailureAttempts(
            startDateTime = startDate.atStartOfDay(),
            endDateTimeExclusive = endDate.plusDays(1).atStartOfDay(),
            requestedBy = requestedBy
        ).associate { it.date to it.count }

        return startDate.datesUntil(endDate.plusDays(1))
            .map { date ->
                MailDailyFailedCountResponse(
                    date = date,
                    failedCount = dailyCountMap[date] ?: 0L
                )
            }
            .toList()
    }

    private fun validateDateRange(startDate: LocalDate, endDate: LocalDate) {
        if (startDate.isAfter(endDate)) {
            throw ApplicationException(REQUEST_VALIDATION_ERROR)
        }
    }
}
