package com.artijjaek.core.ai

import com.artijjaek.core.enums.CategoryType
import com.google.genai.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GeminiClient(
    @Value("\${gemini.api.key}")
    private val GEMINI_API_KEY: String,
    @Value("\${gemini.api.model}")
    private val GEMINI_API_MODEL: String,
    @Value("\${gemini.api.prompt}")
    private val GEMINI_API_PROMPT: String,
) {

    private val log = org.slf4j.LoggerFactory.getLogger(GeminiClient::class.java)

    fun analyzeArticleCategory(title: String, url: String): CategoryType {

        val client = Client.builder().apiKey(GEMINI_API_KEY).build()

        val prompt = GEMINI_API_PROMPT
            .replace("{{categories}}", CategoryType.getAllCategoryTypeString())
            .replace("{{title}}", title)
            .replace("{{url}}", url)

        val response = client.models.generateContent(
            GEMINI_API_MODEL,
            prompt,
            null
        )

        val category = response.text()

        log.info("Category: $category Title: $title URL: $url")
        return CategoryType.fromString(category)
    }

}