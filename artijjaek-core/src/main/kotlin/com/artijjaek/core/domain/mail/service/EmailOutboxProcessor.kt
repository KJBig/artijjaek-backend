package com.artijjaek.core.domain.mail.service

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.common.mail.service.MailService
import com.artijjaek.core.domain.mail.dto.ArticleMailPayload
import com.artijjaek.core.domain.mail.dto.NoticeMailPayload
import com.artijjaek.core.domain.mail.dto.WelcomeMailPayload
import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EmailOutboxProcessor(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val mailService: MailService,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(EmailOutboxProcessor::class.java)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun processIfDue(outboxId: Long, now: LocalDateTime = LocalDateTime.now()): ProcessResult {
        if (!emailOutboxDomainService.claimForSending(outboxId, now)) {
            return ProcessResult(skipped = true, nextRetryAt = null)
        }

        val outbox = emailOutboxDomainService.findById(outboxId)
            ?: return ProcessResult(skipped = true, nextRetryAt = null)

        return try {
            send(outbox)
            markSuccess(outbox, now)
            ProcessResult(skipped = false, nextRetryAt = null)
        } catch (e: Exception) {
            val nextRetryAt = markFailure(outbox, e, now)
            ProcessResult(skipped = false, nextRetryAt = nextRetryAt)
        }
    }

    private fun send(outbox: EmailOutbox) {
        when (outbox.mailType) {
            EmailOutboxType.WELCOME -> {
                val payload = objectMapper.readValue(outbox.payloadJson, WelcomeMailPayload::class.java)
                val member = payload.member
                mailService.sendSubscribeMail(
                    MemberAlertDto(
                        email = member.email,
                        nickname = member.nickname,
                        uuidToken = member.uuidToken
                    )
                )
            }

            EmailOutboxType.ARTICLE -> {
                val payload = objectMapper.readValue(outbox.payloadJson, ArticleMailPayload::class.java)
                val member = payload.member
                val articles = payload.articles.map {
                    ArticleAlertDto(
                        title = it.title,
                        link = it.link,
                        image = it.image,
                        companyLogo = it.companyLogo,
                        companyNameKr = it.companyNameKr
                    )
                }
                mailService.sendArticleMail(
                    MemberAlertDto(
                        email = member.email,
                        nickname = member.nickname,
                        uuidToken = member.uuidToken
                    ),
                    articles
                )
            }

            EmailOutboxType.NOTICE -> {
                val payload = objectMapper.readValue(outbox.payloadJson, NoticeMailPayload::class.java)
                val member = payload.member
                mailService.sendNoticeMail(
                    MemberAlertDto(
                        email = member.email,
                        nickname = member.nickname,
                        uuidToken = member.uuidToken
                    ),
                    payload.title,
                    payload.content
                )
            }
        }
    }

    private fun markSuccess(outbox: EmailOutbox, now: LocalDateTime) {
        outbox.status = EmailOutboxStatus.SENT
        outbox.sentAt = now
        outbox.nextRetryAt = null
        outbox.lastError = null
        emailOutboxDomainService.save(outbox)
    }

    private fun markFailure(outbox: EmailOutbox, throwable: Throwable, now: LocalDateTime): LocalDateTime? {
        val failedAttempts = outbox.attemptCount + 1
        outbox.attemptCount = failedAttempts
        outbox.lastError = buildFailureMessage(throwable)

        if (failedAttempts >= outbox.maxAttempts) {
            outbox.status = EmailOutboxStatus.DEAD
            outbox.nextRetryAt = null
            emailOutboxDomainService.save(outbox)

            log.error("[EmailOutbox] moved to DEAD id={}, attempts={}, error={}", outbox.id, failedAttempts, outbox.lastError)
            return null
        }

        val nextRetryAt = now.plusSeconds(retryDelaySeconds(failedAttempts))
        outbox.status = EmailOutboxStatus.FAIL
        outbox.nextRetryAt = nextRetryAt
        emailOutboxDomainService.save(outbox)

        log.warn(
            "[EmailOutbox] send failed id={}, attempts={}, nextRetryAt={}, error={}",
            outbox.id,
            failedAttempts,
            nextRetryAt,
            outbox.lastError
        )
        return nextRetryAt
    }

    private fun retryDelaySeconds(failedAttempts: Int): Long {
        return when (failedAttempts) {
            1 -> 60L
            2 -> 5 * 60L
            3 -> 15 * 60L
            else -> 60 * 60L
        }
    }

    private fun buildFailureMessage(throwable: Throwable): String {
        val type = throwable::class.simpleName ?: "UnknownException"
        val message = throwable.message ?: "no message"
        return "$type: $message".take(1000)
    }
}

data class ProcessResult(
    val skipped: Boolean,
    val nextRetryAt: LocalDateTime?,
)
