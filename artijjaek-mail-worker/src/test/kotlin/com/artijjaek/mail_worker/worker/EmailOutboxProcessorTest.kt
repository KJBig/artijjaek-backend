package com.artijjaek.mail_worker.worker

import com.artijjaek.core.domain.mail.dto.MemberSnapshot
import com.artijjaek.core.domain.mail.dto.CompanySnapshot
import com.artijjaek.core.domain.mail.dto.NewCompanyMailPayload
import com.artijjaek.core.domain.mail.dto.NoticeMailPayload
import com.artijjaek.core.domain.mail.entity.EmailOutboxAttempt
import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.mail_worker.smtp.MailSendService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class EmailOutboxProcessorTest {

    private val objectMapper = jacksonObjectMapper()

    @InjectMockKs
    lateinit var emailOutboxProcessor: EmailOutboxProcessor

    @MockK
    lateinit var emailOutboxDomainService: EmailOutboxDomainService

    @MockK
    lateinit var mailSendService: MailSendService

    @MockK(relaxed = true)
    lateinit var alertService: EmailOutboxAlertService

    @Test
    @DisplayName("4xx 실패는 FAIL 상태로 재시도 루프를 타며 backoff가 증가한다")
    fun processRetryLoopFor4xxTest() {
        // given
        val outbox = createNoticeOutbox(id = 1L, maxAttempts = 5)
        val firstNow = LocalDateTime.parse("2026-02-28T12:00:00")
        val secondNow = firstNow.plusMinutes(1)

        every { emailOutboxDomainService.claimForSending(1L, any()) } returns true
        every { emailOutboxDomainService.findById(1L) } returns outbox
        every { emailOutboxDomainService.save(any()) } answers { firstArg() }
        every { emailOutboxDomainService.saveAttempt(any()) } answers { firstArg<EmailOutboxAttempt>() }
        every { mailSendService.sendNoticeMail(any(), any(), any()) } throws RuntimeException("421 temporary failure")

        // when
        val first = emailOutboxProcessor.processIfDue(1L, firstNow)
        val second = emailOutboxProcessor.processIfDue(1L, secondNow)

        // then
        Assertions.assertThat(first.skipped).isFalse()
        Assertions.assertThat(first.nextRetryAt).isEqualTo(firstNow.plusMinutes(1))

        Assertions.assertThat(second.skipped).isFalse()
        Assertions.assertThat(second.nextRetryAt).isEqualTo(secondNow.plusMinutes(5))

        Assertions.assertThat(outbox.status).isEqualTo(EmailOutboxStatus.FAIL)
        Assertions.assertThat(outbox.attemptCount).isEqualTo(2)
        verify(exactly = 2) { alertService.notifyFail(1L, any(), any(), any()) }
        verify(exactly = 0) { alertService.notifyDead(any(), any()) }
    }

    @Test
    @DisplayName("재시도 횟수를 초과하면 DEAD 상태로 전환된다")
    fun processDeadWhenExceededMaxAttemptsTest() {
        // given
        val outbox = createNoticeOutbox(id = 2L, maxAttempts = 2)
        val firstNow = LocalDateTime.parse("2026-02-28T12:00:00")
        val secondNow = firstNow.plusMinutes(1)

        every { emailOutboxDomainService.claimForSending(2L, any()) } returns true
        every { emailOutboxDomainService.findById(2L) } returns outbox
        every { emailOutboxDomainService.save(any()) } answers { firstArg() }
        every { emailOutboxDomainService.saveAttempt(any()) } answers { firstArg<EmailOutboxAttempt>() }
        every { mailSendService.sendNoticeMail(any(), any(), any()) } throws RuntimeException("421 temporary failure")

        // when
        emailOutboxProcessor.processIfDue(2L, firstNow)
        val second = emailOutboxProcessor.processIfDue(2L, secondNow)

        // then
        Assertions.assertThat(second.nextRetryAt).isNull()
        Assertions.assertThat(outbox.status).isEqualTo(EmailOutboxStatus.DEAD)
        Assertions.assertThat(outbox.attemptCount).isEqualTo(2)
        Assertions.assertThat(outbox.nextRetryAt).isNull()
        verify(exactly = 1) { alertService.notifyDead(2L, any()) }
    }

    @Test
    @DisplayName("5xx 실패는 재시도 없이 즉시 DEAD 상태로 전환된다")
    fun processImmediateDeadFor5xxTest() {
        // given
        val outbox = createNoticeOutbox(id = 3L, maxAttempts = 5)
        val now = LocalDateTime.parse("2026-02-28T12:00:00")

        every { emailOutboxDomainService.claimForSending(3L, any()) } returns true
        every { emailOutboxDomainService.findById(3L) } returns outbox
        every { emailOutboxDomainService.save(any()) } answers { firstArg() }
        every { emailOutboxDomainService.saveAttempt(any()) } answers { firstArg<EmailOutboxAttempt>() }
        every {
            mailSendService.sendNoticeMail(
                any(),
                any(),
                any()
            )
        } throws RuntimeException("554 upstream service error")

        // when
        val result = emailOutboxProcessor.processIfDue(3L, now)

        // then
        Assertions.assertThat(result.skipped).isFalse()
        Assertions.assertThat(result.nextRetryAt).isNull()
        Assertions.assertThat(outbox.status).isEqualTo(EmailOutboxStatus.DEAD)
        Assertions.assertThat(outbox.attemptCount).isEqualTo(1)
        Assertions.assertThat(outbox.lastError).startsWith("PERMANENT|")
        verify(exactly = 0) { alertService.notifyFail(any(), any(), any(), any()) }
        verify(exactly = 1) { alertService.notifyDead(3L, any()) }
    }

    @Test
    @DisplayName("NEW_COMPANY 타입은 신규 회사 안내 메일을 발송한다")
    fun processNewCompanyMailTypeTest() {
        // given
        val payload = NewCompanyMailPayload(
            member = MemberSnapshot(
                email = "test@test.com",
                nickname = "tester",
                uuidToken = "uuid-token"
            ),
            companies = listOf(
                CompanySnapshot(
                    nameKr = "회사A",
                    nameEn = "CompanyA",
                    logo = "logo-a",
                    blogUrl = "blog-a"
                )
            )
        )
        val outbox = EmailOutbox(
            id = 4L,
            mailType = EmailOutboxType.NEW_COMPANY,
            recipientEmail = "test@test.com",
            subject = "[아티짹] 신규 구독 회사가 추가되었어요",
            payloadJson = objectMapper.writeValueAsString(payload),
            status = EmailOutboxStatus.PENDING,
            maxAttempts = 5,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            requestedAt = LocalDateTime.parse("2026-02-28T11:00:00")
        )
        val now = LocalDateTime.parse("2026-02-28T12:00:00")

        every { emailOutboxDomainService.claimForSending(4L, any()) } returns true
        every { emailOutboxDomainService.findById(4L) } returns outbox
        every { emailOutboxDomainService.save(any()) } answers { firstArg() }
        every { emailOutboxDomainService.saveAttempt(any()) } answers { firstArg<EmailOutboxAttempt>() }
        every { mailSendService.sendNewCompanyMail(any(), any()) } returns Unit

        // when
        val result = emailOutboxProcessor.processIfDue(4L, now)

        // then
        Assertions.assertThat(result.skipped).isFalse()
        Assertions.assertThat(result.nextRetryAt).isNull()
        Assertions.assertThat(outbox.status).isEqualTo(EmailOutboxStatus.SENT)
        verify(exactly = 1) { mailSendService.sendNewCompanyMail(any(), any()) }
    }

    private fun createNoticeOutbox(id: Long, maxAttempts: Int): EmailOutbox {
        val payload = NoticeMailPayload(
            member = MemberSnapshot(
                email = "test@test.com",
                nickname = "tester",
                uuidToken = "uuid-token"
            ),
            title = "notice-title",
            content = "notice-content"
        )
        return EmailOutbox(
            id = id,
            mailType = EmailOutboxType.NOTICE,
            recipientEmail = "test@test.com",
            subject = "[아티짹] notice-title",
            payloadJson = objectMapper.writeValueAsString(payload),
            status = EmailOutboxStatus.PENDING,
            maxAttempts = maxAttempts,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            requestedAt = LocalDateTime.parse("2026-02-28T11:00:00")
        )
    }
}
