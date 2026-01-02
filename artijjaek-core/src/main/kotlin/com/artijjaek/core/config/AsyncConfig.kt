package com.artijjaek.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
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

}
