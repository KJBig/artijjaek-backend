package com.server.noati

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NoatiApplication

fun main(args: Array<String>) {
	runApplication<NoatiApplication>(*args)
}
