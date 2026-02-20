package com.artijjaek.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableAsync
class AsyncConfig {

    @Bean(name = ["asyncThreadPoolExecutor"])
    fun getAlertExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.setCorePoolSize(30)
        executor.setMaxPoolSize(60)
        executor.setQueueCapacity(600)
        executor.setThreadNamePrefix("Artijjaek-Async-Thread-")
        executor.initialize()
        return executor
    }

    @Bean(name = ["asyncEmailThreadPoolExecutor"])
    fun getEmailExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.setCorePoolSize(2)
        executor.setMaxPoolSize(4)
        executor.setQueueCapacity(1000)
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.setThreadNamePrefix("Artijjaek-Email-Thread-")
        executor.initialize()
        return executor
    }

}
