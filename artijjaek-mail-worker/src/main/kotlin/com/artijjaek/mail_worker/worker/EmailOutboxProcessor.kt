package com.artijjaek.mail_worker.worker

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.domain.mail.dto.ArticleMailPayload
import com.artijjaek.core.domain.mail.dto.NoticeMailPayload
import com.artijjaek.core.domain.mail.dto.ProcessResult
import com.artijjaek.core.domain.mail.dto.WelcomeMailPayload
import com.artijjaek.core.domain.mail.entity.EmailOutboxAttempt
import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxAttemptResult
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.artijjaek.core.domain.mail.enums.MailFailureType
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.mail_worker.smtp.MailSendService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.mail.MailAuthenticationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class EmailOutboxProcessor(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val mailSendService: MailSendService,
    private val objectMapper: ObjectMapper,
    private val alertService: EmailOutboxAlertService,
) {
    private val log = LoggerFactory.getLogger(EmailOutboxProcessor::class.java)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun processIfDue(outboxId: Long, now: LocalDateTime = LocalDateTime.now()): ProcessResult {
        val claimedForSending = emailOutboxDomainService.claimForSending(outboxId, now)
        if (!claimedForSending) {
            return ProcessResult(skipped = true, nextRetryAt = null)
        }

        val outbox = emailOutboxDomainService.findById(outboxId)
            ?: return ProcessResult(skipped = true, nextRetryAt = null)

        return try {
            send(outbox)
            markSuccess(outbox, now)
            alertService.recordSuccess()
            ProcessResult(skipped = false, nextRetryAt = null)
        } catch (e: Exception) {
            val nextRetryAt = markFailure(outbox, e, now)
            alertService.recordFailure()
            ProcessResult(skipped = false, nextRetryAt = nextRetryAt)
        }
    }

    private fun send(outbox: EmailOutbox) {
        when (outbox.mailType) {
            EmailOutboxType.WELCOME -> {
                val payload = objectMapper.readValue(outbox.payloadJson, WelcomeMailPayload::class.java)
                val member = payload.member
                mailSendService.sendSubscribeMail(
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
                mailSendService.sendArticleMail(
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
                mailSendService.sendNoticeMail(
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
        val attemptNo = outbox.attemptCount + 1
        outbox.status = EmailOutboxStatus.SENT
        outbox.sentAt = now
        outbox.nextRetryAt = null
        outbox.lastError = null
        emailOutboxDomainService.save(outbox)
        emailOutboxDomainService.saveAttempt(
            EmailOutboxAttempt(
                emailOutbox = outbox,
                attemptNo = attemptNo,
                result = EmailOutboxAttemptResult.SUCCESS,
                requestedBy = outbox.requestedBy,
                occurredAt = now
            )
        )
    }

    private fun markFailure(outbox: EmailOutbox, throwable: Throwable, now: LocalDateTime): LocalDateTime? {
        val failedAttempts = outbox.attemptCount + 1
        val failureType = classifyFailure(throwable)

        outbox.attemptCount = failedAttempts
        outbox.lastError = buildFailureMessage(failureType, throwable)

        if (failureType == MailFailureType.PERMANENT || failedAttempts >= outbox.maxAttempts) {
            outbox.status = EmailOutboxStatus.DEAD
            outbox.nextRetryAt = null
            emailOutboxDomainService.save(outbox)
            saveFailureAttempt(outbox, failedAttempts, now)
            alertService.notifyDead(outbox.id, outbox.lastError)

            log.error(
                "[EmailOutbox] moved to DEAD id={}, attempts={}, error={}",
                outbox.id,
                failedAttempts,
                outbox.lastError
            )
            return null
        }

        val nextRetryAt = now.plusSeconds(retryDelaySeconds(failedAttempts))
        outbox.status = EmailOutboxStatus.FAIL
        outbox.nextRetryAt = nextRetryAt
        emailOutboxDomainService.save(outbox)
        saveFailureAttempt(outbox, failedAttempts, now)
        alertService.notifyFail(outbox.id, failedAttempts, nextRetryAt, outbox.lastError)

        log.warn(
            "[EmailOutbox] send failed id={}, attempts={}, nextRetryAt={}, error={}",
            outbox.id,
            failedAttempts,
            nextRetryAt,
            outbox.lastError
        )
        return nextRetryAt
    }

    private fun saveFailureAttempt(outbox: EmailOutbox, attemptNo: Int, now: LocalDateTime) {
        emailOutboxDomainService.saveAttempt(
            EmailOutboxAttempt(
                emailOutbox = outbox,
                attemptNo = attemptNo,
                result = EmailOutboxAttemptResult.FAIL,
                errorMessage = outbox.lastError,
                requestedBy = outbox.requestedBy,
                occurredAt = now
            )
        )
    }

    private fun retryDelaySeconds(failedAttempts: Int): Long {
        return when (failedAttempts) {
            1 -> 60L
            2 -> 5 * 60L
            3 -> 15 * 60L
            else -> 60 * 60L
        }
    }

    private fun buildFailureMessage(failureType: MailFailureType, throwable: Throwable): String {
        val type = throwable::class.simpleName ?: "UnknownException"
        val message = throwable.message ?: "no message"
        return "$failureType|$type: $message".take(1000)
    }

    private fun classifyFailure(throwable: Throwable): MailFailureType {
        if (throwable is MailAuthenticationException) {
            return MailFailureType.PERMANENT
        }

        val message = collectMessages(throwable)
        val lowerMessage = message.lowercase(Locale.ROOT)

        if (containsSmtp5xx(message)) {
            return MailFailureType.PERMANENT
        }

        if (containsSmtp4xx(message)) {
            return MailFailureType.TRANSIENT
        }

        val permanentSignals = listOf(
            "550",
            "user unknown",
            "invalid address",
            "mailbox unavailable",
            "address rejected",
            "recipient address rejected",
        )
        if (permanentSignals.any { lowerMessage.contains(it) }) {
            return MailFailureType.PERMANENT
        }

        val transientSignals = listOf(
            "421",
            "timeout",
            "temporar",
            "connection reset",
            "could not connect",
            "read timed out",
        )
        if (transientSignals.any { lowerMessage.contains(it) }) {
            return MailFailureType.TRANSIENT
        }

        return MailFailureType.TRANSIENT
    }

    private fun containsSmtp5xx(message: String): Boolean {
        return Regex("""\b5\d{2}\b""").containsMatchIn(message)
    }

    private fun containsSmtp4xx(message: String): Boolean {
        return Regex("""\b4\d{2}\b""").containsMatchIn(message)
    }

    private fun collectMessages(throwable: Throwable): String {
        val builder = StringBuilder()
        var current: Throwable? = throwable
        while (current != null) {
            if (builder.isNotEmpty()) {
                builder.append(" | ")
            }
            builder.append(current.message ?: current::class.simpleName ?: "unknown")
            current = current.cause
        }
        return builder.toString()
    }

}
