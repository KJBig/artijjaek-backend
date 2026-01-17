package com.artijjaek.core.ai

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.category.entity.Category
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

        log.info("[시작!!!] analyzeArticleCategory() in GeminiClient.class")
        val client = Client.builder().apiKey(GEMINI_API_KEY).build()

        return try {
            val categoryString = getCategoryString(categories)
            log.info("getCategoryString() 성공 in GeminiClient.class")

            val promptHeader = GEMINI_API_PROMPT.replace("{{categories}}", categoryString)
            val prompt = StringBuilder(promptHeader)

            for (index in articles.indices) {
                val nowArticle = articles[index]
                prompt.append(buildArticleData(index, nowArticle.title, nowArticle.link))
            }
            log.info("프롬프트 생성 성공 in GeminiClient.class")

            val finalPrompt = prompt.toString()

            val response = client.models.generateContent(
                GEMINI_API_MODEL,
                finalPrompt,
                null
            )

            log.info("제미나이 클라이언트 만들기 생성 성공 in GeminiClient.class")

            log.info("Gemini response text: ${response.text()}")

            response.text()
                .lines()
                .filter { it.isNotBlank() }
                .map { it.trim().split(":") }
                .associate { (num, category) ->
                    num.toInt() to getCategory(category, categories)
                }

        } catch (e: Exception) {
            log.error(
                """
            [Gemini Analyze Error]
            articleCount=${articles.size}
            categoryCount=${categories.size}
            errorMessage=${e.message}
            """.trimIndent(),
                e
            )

            throw IllegalStateException()

        }
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