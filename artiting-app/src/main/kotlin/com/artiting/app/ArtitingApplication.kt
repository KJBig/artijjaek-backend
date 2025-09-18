package com.artiting.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["com.artiting.admin", "com.artiting.api", "com.artiting.batch", "com.artiting.core"])
class ArtitingApplication

fun main(args: Array<String>) {
    runApplication<ArtitingApplication>(*args)
}
