package com.artijjaek.mail_worker.worker

import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.core.webhook.WebHookService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class EmailOutboxAlertService(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val webHookService: WebHookService,
) {
    private val log = LoggerFactory.getLogger(EmailOutboxAlertService::class.java)
    private val lock = Any()

    private var windowStart: LocalDateTime = LocalDateTime.now()
    private var successCount: Long = 0
    private var failureCount: Long = 0
    private var lastBacklogAlertAt: LocalDateTime? = null

    fun recordSuccess() {
        synchronized(lock) {
            rollWindowIfNeeded(LocalDateTime.now())
            successCount++
        }
    }

    fun recordFailure() {
        synchronized(lock) {
            rollWindowIfNeeded(LocalDateTime.now())
            failureCount++
        }
    }

    fun notifyDead(outboxId: Long?, lastError: String?) {
        log.error("[EmailOutbox][Critical] DEAD 발생 id={}, error={}", outboxId, lastError)
        webHookService.sendMailErrorMessage(outboxId, lastError)
    }

    fun notifyFail(outboxId: Long?, attempts: Int, nextRetryAt: LocalDateTime?, lastError: String?) {
        val content = """
            ⚠️ [메일발송 실패(재시도 예정)]
            outboxId: $outboxId
            attempts: $attempts
            nextRetryAt: $nextRetryAt
            error: ${lastError ?: "unknown"}
            time: ${LocalDateTime.now()}
        """.trimIndent()
        sendDiscordAlert(content)
    }

    fun checkBacklog(now: LocalDateTime = LocalDateTime.now()) {
        val oldestDueBaseAt = emailOutboxDomainService.findOldestDueRequestedAt(now) ?: return
        val delayedMinutes = Duration.between(oldestDueBaseAt, now).toMinutes()
        if (delayedMinutes >= 5) {
            log.warn(
                "[EmailOutbox][Backlog] due queue 지연 감지 oldestDueAt={}, delayedMinutes={}",
                oldestDueBaseAt,
                delayedMinutes
            )
            val nowTime = LocalDateTime.now()
            val shouldAlert =
                lastBacklogAlertAt == null || Duration.between(lastBacklogAlertAt, nowTime).toMinutes() >= 5
            if (shouldAlert) {
                sendDiscordAlert(
                    """
                    [메일발송 적체 경고]
                    oldestDueAt: $oldestDueBaseAt
                    delayedMinutes: $delayedMinutes
                    time: $nowTime
                    """.trimIndent()
                )
                lastBacklogAlertAt = nowTime
            }
        }
    }

    private fun rollWindowIfNeeded(now: LocalDateTime) {
        val elapsedMinutes = Duration.between(windowStart, now).toMinutes()
        if (elapsedMinutes < 10) {
            return
        }

        val total = successCount + failureCount
        if (total > 0) {
            val failureRate = failureCount.toDouble() / total.toDouble() * 100.0
            when {
                failureRate > 25.0 -> log.error(
                    "[EmailOutbox][Quality] 실패율 치명: failureRate={}%, success={}, failure={}, windowStart={}, windowEnd={}",
                    String.format("%.2f", failureRate), successCount, failureCount, windowStart, now
                ).also {
                    sendDiscordAlert(
                        """
                        [메일발송 품질 치명]
                        failureRate: ${String.format("%.2f", failureRate)}%
                        success: $successCount
                        failure: $failureCount
                        windowStart: $windowStart
                        windowEnd: $now
                        """.trimIndent()
                    )
                }

                failureRate > 10.0 -> log.warn(
                    "[EmailOutbox][Quality] 실패율 경고: failureRate={}%, success={}, failure={}, windowStart={}, windowEnd={}",
                    String.format("%.2f", failureRate), successCount, failureCount, windowStart, now
                ).also {
                    sendDiscordAlert(
                        """
                        [메일발송 품질 경고]
                        failureRate: ${String.format("%.2f", failureRate)}%
                        success: $successCount
                        failure: $failureCount
                        windowStart: $windowStart
                        windowEnd: $now
                        """.trimIndent()
                    )
                }
            }
        }

        windowStart = now
        successCount = 0
        failureCount = 0
    }

    private fun sendDiscordAlert(content: String) {
        try {
            webHookService.sendMailAlertMessage(content)
        } catch (e: Exception) {
            log.error("[EmailOutbox][Alert] discord webhook 전송 실패", e)
        }
    }
}
