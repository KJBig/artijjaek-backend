package com.noati

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["com.noati.core", "com.noati.batch"])
class BatchApplication

fun main(args: Array<String>) {
    runApplication<BatchApplication>(*args)
}