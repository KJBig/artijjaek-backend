package com.artijjaek.core.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableAsync
class AsyncConfig {
    @Value("\${async.alert.core-pool-size:30}")
    private var alertCorePoolSize: Int = 30

    @Value("\${async.alert.max-pool-size:60}")
    private var alertMaxPoolSize: Int = 60

    @Value("\${async.alert.queue-capacity:600}")
    private var alertQueueCapacity: Int = 600

    @Value("\${async.alert.thread-name-prefix:Artijjaek-Async-Thread-}")
    private var alertThreadNamePrefix: String = "Artijjaek-Async-Thread-"

    @Value("\${async.email-dispatch.core-pool-size:4}")
    private var emailDispatchCorePoolSize: Int = 4

    @Value("\${async.email-dispatch.max-pool-size:4}")
    private var emailDispatchMaxPoolSize: Int = 4

    @Value("\${async.email-dispatch.queue-capacity:100}")
    private var emailDispatchQueueCapacity: Int = 100

    @Value("\${async.email-dispatch.thread-name-prefix:Artijjaek-Email-Thread-}")
    private var emailDispatchThreadNamePrefix: String = "Artijjaek-Email-Thread-"

    @Value("\${async.mail-relay.core-pool-size:1}")
    private var mailRelayCorePoolSize: Int = 1

    @Value("\${async.mail-relay.max-pool-size:1}")
    private var mailRelayMaxPoolSize: Int = 1

    @Value("\${async.mail-relay.queue-capacity:100}")
    private var mailRelayQueueCapacity: Int = 100

    @Value("\${async.mail-worker.core-pool-size:1}")
    private var mailWorkerCorePoolSize: Int = 1

    @Value("\${async.mail-worker.max-pool-size:1}")
    private var mailWorkerMaxPoolSize: Int = 1

    @Value("\${async.mail-worker.queue-capacity:10}")
    private var mailWorkerQueueCapacity: Int = 10

    @Bean(name = ["asyncThreadPoolExecutor"])
    fun getAlertExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.setCorePoolSize(alertCorePoolSize)
        executor.setMaxPoolSize(alertMaxPoolSize)
        executor.setQueueCapacity(alertQueueCapacity)
        executor.setThreadNamePrefix(alertThreadNamePrefix)
        executor.initialize()
        return executor
    }

    @Bean(name = ["asyncEmailThreadPoolExecutor"])
    fun getEmailExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.setCorePoolSize(emailDispatchCorePoolSize)
        executor.setMaxPoolSize(emailDispatchMaxPoolSize)
        executor.setQueueCapacity(emailDispatchQueueCapacity)
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.setThreadNamePrefix(emailDispatchThreadNamePrefix)
        executor.initialize()
        return executor
    }

    @Bean(name = ["mailRelayExecutor"])
    fun mailRelayExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.setCorePoolSize(mailRelayCorePoolSize)
        executor.setMaxPoolSize(mailRelayMaxPoolSize)
        executor.setQueueCapacity(mailRelayQueueCapacity)
        executor.setThreadNamePrefix("Mail-Relay-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(20)
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.initialize()
        return executor
    }

    @Bean(name = ["mailWorkerExecutor"])
    fun mailWorkerExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.setCorePoolSize(mailWorkerCorePoolSize)
        executor.setMaxPoolSize(mailWorkerMaxPoolSize)
        executor.setQueueCapacity(mailWorkerQueueCapacity)
        executor.setThreadNamePrefix("Mail-Worker-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(20)
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.initialize()
        return executor
    }

}
