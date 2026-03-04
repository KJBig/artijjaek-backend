package com.artijjaek.mq.processor

import com.artijjaek.core.domain.mail.queue.trigger.MailDispatchTrigger
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class OutboxDuePollingSchedulerTest {

    @InjectMockKs
    lateinit var scheduler: OutboxDuePollingScheduler

    @MockK
    lateinit var emailOutboxDomainService: EmailOutboxDomainService

    @MockK
    lateinit var mailDispatchTrigger: MailDispatchTrigger

    @Test
    @DisplayName("due outbox가 존재하면 dispatch를 호출한다")
    fun triggerWhenDueExistsTest() {
        every { emailOutboxDomainService.existsDue(any()) } returns true
        justRun { mailDispatchTrigger.dispatchDueOutboxes(any()) }

        scheduler.triggerWhenDueExists()

        verify(exactly = 1) { mailDispatchTrigger.dispatchDueOutboxes(any()) }
    }

    @Test
    @DisplayName("due outbox가 없으면 dispatch를 호출하지 않는다")
    fun skipWhenNoDueOutboxTest() {
        every { emailOutboxDomainService.existsDue(any()) } returns false

        scheduler.triggerWhenDueExists()

        verify(exactly = 0) { mailDispatchTrigger.dispatchDueOutboxes(any()) }
    }
}
