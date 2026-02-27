package com.artijjaek.core.domain.mail.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import jakarta.annotation.PreDestroy

@Component
class EmailOutboxWorkerCoordinator(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val emailOutboxProcessor: EmailOutboxProcessor,
    private val retryWakeupScheduler: EmailOutboxRetryWakeupScheduler,
) {
    private val log = LoggerFactory.getLogger(EmailOutboxWorkerCoordinator::class.java)

    private val running = AtomicBoolean(false)
    private val mainExecutor: ExecutorService = Executors.newSingleThreadExecutor(namedThreadFactory("email-outbox-main"))
    private val senderExecutor: ExecutorService = Executors.newFixedThreadPool(4, namedThreadFactory("email-outbox-sender"))

    fun triggerProcessing() {
        if (!running.compareAndSet(false, true)) {
            return
        }
        mainExecutor.submit {
            try {
                processLoop()
            } finally {
                running.set(false)
                if (emailOutboxDomainService.existsDue(LocalDateTime.now())) {
                    triggerProcessing()
                } else {
                    retryWakeupScheduler.scheduleEarliestRetry { triggerProcessing() }
                }
            }
        }
    }

    private fun processLoop() {
        while (true) {
            val dueIds = emailOutboxDomainService.findDueIds(LocalDateTime.now(), 50)
            if (dueIds.isEmpty()) {
                return
            }

            val futures = dueIds.map { id ->
                CompletableFuture.runAsync(
                    { emailOutboxProcessor.processIfDue(id, LocalDateTime.now()) },
                    senderExecutor
                )
            }
            futures.forEach { it.join() }
            log.info("[EmailOutbox] processed batch size={}", dueIds.size)
        }
    }

    @PreDestroy
    fun shutdownExecutors() {
        mainExecutor.shutdown()
        senderExecutor.shutdown()
    }

    private fun namedThreadFactory(prefix: String): ThreadFactory {
        val index = AtomicInteger(1)
        return ThreadFactory { runnable ->
            Thread(runnable, "$prefix-${index.getAndIncrement()}")
        }
    }
}
