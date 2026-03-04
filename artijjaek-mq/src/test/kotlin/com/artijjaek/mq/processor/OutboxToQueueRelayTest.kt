package com.artijjaek.mq.processor

import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.mq.queue.MailQueueChannel
import com.artijjaek.mq.queue.MailQueueMessage
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class OutboxToQueueRelayTest {

    @MockK
    lateinit var emailOutboxDomainService: EmailOutboxDomainService

    @MockK
    lateinit var mailQueueChannel: MailQueueChannel

    private val relayExecutor = ThreadPoolTaskExecutor().apply {
        corePoolSize = 1
        maxPoolSize = 1
        setQueueCapacity(10)
        setThreadNamePrefix("relay-test-")
        initialize()
    }

    private val relay by lazy {
        OutboxToQueueRelay(
            emailOutboxDomainService = emailOutboxDomainService,
            mailQueueChannel = mailQueueChannel,
            relayExecutor = relayExecutor
        )
    }

    @AfterEach
    fun tearDown() {
        relayExecutor.shutdown()
    }

    @Test
    @DisplayName("dispatchOutbox는 queue publish와 ENQUEUED 마킹을 수행한다")
    fun dispatchOutboxTest() {
        justRun { mailQueueChannel.publish(any()) }
        every { emailOutboxDomainService.markEnqueued(1L, any()) } returns true

        relay.dispatchOutbox(1L)

        verify(exactly = 1) { mailQueueChannel.publish(MailQueueMessage(1L)) }
        verify(exactly = 1) { emailOutboxDomainService.markEnqueued(1L, any()) }
    }

    @Test
    @DisplayName("dispatchDueOutboxes는 due ids를 큐에 적재한다")
    fun dispatchDueOutboxesTest() {
        justRun { mailQueueChannel.publish(any()) }
        every { emailOutboxDomainService.findDueIds(any(), 2) } returnsMany listOf(listOf(1L, 2L), emptyList())
        every { emailOutboxDomainService.markEnqueued(any(), any()) } returns true

        relay.dispatchDueOutboxes(2)

        awaitUntil {
            verify(exactly = 1) { mailQueueChannel.publish(MailQueueMessage(1L)) }
            verify(exactly = 1) { mailQueueChannel.publish(MailQueueMessage(2L)) }
            verify(exactly = 2) { emailOutboxDomainService.markEnqueued(any(), any()) }
        }
    }

    private fun awaitUntil(assertion: () -> Unit) {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2)
        var lastError: AssertionError? = null

        while (System.nanoTime() < deadline) {
            try {
                assertion()
                return
            } catch (e: AssertionError) {
                lastError = e
                Thread.sleep(20)
            }
        }

        throw lastError ?: AssertionError("condition not satisfied")
    }
}
