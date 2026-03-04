package com.artijjaek.mail_worker.worker

import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.core.webhook.WebHookService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class EmailOutboxAlertServiceTest {

    @InjectMockKs
    lateinit var alertService: EmailOutboxAlertService

    @MockK
    lateinit var emailOutboxDomainService: EmailOutboxDomainService

    @MockK(relaxed = true)
    lateinit var webHookService: WebHookService

    @Test
    @DisplayName("FAIL 알림은 디스코드 웹훅으로 발송된다")
    fun notifyFailSendsWebhookTest() {
        // when
        alertService.notifyFail(
            outboxId = 10L,
            attempts = 2,
            nextRetryAt = LocalDateTime.parse("2026-03-04T17:30:00"),
            lastError = "TRANSIENT|RuntimeException: timeout"
        )

        // then
        verify(exactly = 1) {
            webHookService.sendMailAlertMessage(
                withArg { message ->
                    assert(message.contains("메일발송 실패(재시도 예정)"))
                    assert(message.contains("outboxId: 10"))
                    assert(message.contains("attempts: 2"))
                }
            )
        }
    }

    @Test
    @DisplayName("backlog 지연이 5분 이상이면 웹훅을 발송한다")
    fun checkBacklogSendsWebhookWhenDelayedTest() {
        // given
        val now = LocalDateTime.parse("2026-03-04T18:00:00")
        every { emailOutboxDomainService.findOldestDueRequestedAt(now) } returns now.minusMinutes(7)

        // when
        alertService.checkBacklog(now)

        // then
        verify(exactly = 1) {
            webHookService.sendMailAlertMessage(
                withArg { message ->
                    assert(message.contains("메일발송 적체 경고"))
                    assert(message.contains("delayedMinutes: 7"))
                }
            )
        }
    }
}
