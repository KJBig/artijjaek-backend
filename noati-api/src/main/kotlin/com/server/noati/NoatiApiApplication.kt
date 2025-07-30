package com.server.noati

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NoatiApiApplication

fun main(args: Array<String>) {
    runApplication<NoatiApiApplication>(*args)
}
