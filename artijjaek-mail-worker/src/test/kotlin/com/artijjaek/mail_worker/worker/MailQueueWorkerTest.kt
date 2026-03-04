package com.artijjaek.mail_worker.worker

import com.artijjaek.mq.queue.MailQueueChannel
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.ReflectionTestUtils

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class MailQueueWorkerTest {

    @InjectMockKs
    lateinit var mailQueueWorker: MailQueueWorker

    @MockK
    lateinit var mailQueueChannel: MailQueueChannel

    @MockK
    lateinit var emailOutboxProcessor: EmailOutboxProcessor

    @MockK
    lateinit var workerExecutor: ThreadPoolTaskExecutor

    @Test
    @DisplayName("consumer-count 만큼 worker 태스크를 실행한다")
    fun startWithConfiguredConsumerCountTest() {
        ReflectionTestUtils.setField(mailQueueWorker, "consumerCount", 3)
        justRun { workerExecutor.execute(any()) }

        mailQueueWorker.start()

        verify(exactly = 3) { workerExecutor.execute(any()) }
    }

    @Test
    @DisplayName("consumer-count가 0 이하면 최소 1개로 실행한다")
    fun startWithInvalidConsumerCountTest() {
        ReflectionTestUtils.setField(mailQueueWorker, "consumerCount", 0)
        justRun { workerExecutor.execute(any()) }

        mailQueueWorker.start()

        verify(exactly = 1) { workerExecutor.execute(any()) }
    }
}
