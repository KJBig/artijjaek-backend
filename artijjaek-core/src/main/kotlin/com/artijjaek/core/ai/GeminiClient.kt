package com.artijjaek.core.ai

import com.artijjaek.core.domain.Article
import com.artijjaek.core.domain.Category
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

    fun analyzeArticleCategory(articles: List<Article>, categories: List<Category>): Map<Int, Category> {

        val client = Client.builder().apiKey(GEMINI_API_KEY).build()

        val categoryString = getCategoryString(categories)
        var promptHeader = GEMINI_API_PROMPT.replace("{{categories}}", categoryString)
        val prompt = StringBuilder(promptHeader)

        for (index in articles.indices) {
            val nowArticle = articles[index]
            prompt.append(buildArticleData(index, nowArticle.title, nowArticle.link))
        }

        promptHeader = prompt.toString()
        val response = client.models.generateContent(
            GEMINI_API_MODEL,
            promptHeader,
            null
        )
        log.info("Response: $response.text()")
        return response.text().lines()
            .filter { it.isNotBlank() }
            .map { it.trim().split(":") }
            .associate { (num, category) -> num.toInt() to getCategory(category, categories) }
    }

    private fun getCategory(category: String, categories: List<Category>): Category {
        for (item in categories) {
            if (item.name == category) {
                return item
            }
        }

        throw IllegalStateException("카테고리를 찾을 수 없습니다.")
    }

    private fun getCategoryString(categories: List<Category>): String {
        val stringBuilder = StringBuilder("[")

        for (i in categories.indices) {
            stringBuilder.append(categories[i].name)
            if (i == categories.size - 1) {
                stringBuilder.append("]")
            } else {
                stringBuilder.append(", ")
            }
        }

        return stringBuilder.toString()
    }

    private fun buildArticleData(index: Int, title: String, url: String): String {
        return GEMINI_API_PROMPT_ARTICLE_DATA.replace("{{index}}", index.toString())
            .replace("{{title}}", title)
            .replace("{{url}}", url)
    }

}