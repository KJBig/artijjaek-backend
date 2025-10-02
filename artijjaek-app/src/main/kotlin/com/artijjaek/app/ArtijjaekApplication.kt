package com.artijjaek.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["com.artijjaek.admin", "com.artijjaek.api", "com.artijjaek.batch", "com.artijjaek.core"])
class ArtijjaekApplication

fun main(args: Array<String>) {
    runApplication<ArtijjaekApplication>(*args)
}
