package com.artijjaek.core.ai

import com.artijjaek.core.domain.Article
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
    @Value("\${gemini.api.article-data}")
    private val GEMINI_API_PROMPT_ARTICLE_DATA: String,

    ) {

    private val log = org.slf4j.LoggerFactory.getLogger(GeminiClient::class.java)

    //    fun analyzeArticleCategory(title: String, url: String): CategoryType {
    fun analyzeArticleCategory(articles: List<Article>): Map<Int, CategoryType> {

        val client = Client.builder().apiKey(GEMINI_API_KEY).build()

        var prompt = GEMINI_API_PROMPT
            .replace("{{categories}}", CategoryType.getAllCategoryTypeString())
        val promptBuilder = StringBuilder(prompt)

        for (index in articles.indices) {
            val nowArticle = articles[index]
            promptBuilder.append(buildArticleData(index, nowArticle.title, nowArticle.link))
        }

        prompt = promptBuilder.toString()
        val response = client.models.generateContent(
            GEMINI_API_MODEL,
            prompt,
            null
        )
        log.info("Response: $response.text()")
        return response.text().lines()
            .filter { it.isNotBlank() }
            .map { it.trim().split(":") }
            .associate { (num, category) -> num.toInt() to CategoryType.fromString(category) }
    }

    private fun buildArticleData(index: Int, title: String, url: String): String {
        return GEMINI_API_PROMPT_ARTICLE_DATA.replace("{{index}}", index.toString())
            .replace("{{title}}", title)
            .replace("{{url}}", url)
    }

}