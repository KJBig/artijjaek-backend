package com.artijjaek.core.webhook

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DiscordWebHookConnector {

    private val log = LoggerFactory.getLogger(DiscordWebHookConnector::class.java)
    private val objectMapper = jacksonObjectMapper()
    private val client = OkHttpClient()

    fun sendMessageForDiscord(message: WebHookMessage, url: String) {
        val jsonMessage = objectMapper.writeValueAsString(message)
        val requestBody = jsonMessage.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                log.info("Discord status=${response.code}, message=${response.message}, body=$body")
            }
        } catch (e: Exception) {
            throw IllegalStateException(e.message)
        }
    }
}