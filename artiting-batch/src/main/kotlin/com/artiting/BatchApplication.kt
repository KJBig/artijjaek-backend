package com.artiting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["com.artiting.core", "com.artiting.batch"])
class BatchApplication

fun main(args: Array<String>) {
    runApplication<BatchApplication>(*args)
}