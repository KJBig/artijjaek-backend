package com.artijjaek.mail_worker.worker

import com.artijjaek.mq.queue.MailQueueChannel
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class MailQueueWorker(
    private val mailQueueChannel: MailQueueChannel,
    private val emailOutboxProcessor: EmailOutboxProcessor,
    @Qualifier("mailWorkerExecutor")
    private val workerExecutor: ThreadPoolTaskExecutor,
) {
    @Value("\${async.mail-worker.consumer-count:1}")
    private var consumerCount: Int = 1

    private val log = LoggerFactory.getLogger(MailQueueWorker::class.java)
    private val running = AtomicBoolean(true)

    @PostConstruct
    fun start() {
        val startCount = if (consumerCount > 0) consumerCount else 1
        repeat(startCount) {
            workerExecutor.execute {
                while (running.get()) {
                    try {
                        val message = mailQueueChannel.take()
                        emailOutboxProcessor.processIfDue(message.outboxId)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    } catch (e: Exception) {
                        log.error("[MailWorker] consume/process failed", e)
                    }
                }
            }
        }
    }

    @PreDestroy
    fun shutdown() {
        running.set(false)
    }
}
