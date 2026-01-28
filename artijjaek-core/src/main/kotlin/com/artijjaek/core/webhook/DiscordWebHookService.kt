package com.artijjaek.core.webhook

import com.artijjaek.core.common.mail.dto.ArticleMailDto
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.inquiry.entity.Inquiry
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DiscordWebHookService(
    private val discordWebHookConnector: DiscordWebHookConnector,
) : WebHookService {

    @Value("\${discord.webhook.new-article}")
    lateinit var DISCORD_NEW_ARTICLE_URL: String;

    @Value("\${discord.webhook.new-inquiry}")
    lateinit var DISCORD_NEW_INQUIRY_URL: String;

    @Value("\${discord.webhook.category-allocation}")
    lateinit var DISCORD_CATEGORY_ALLOCATION_URL: String;

    @Async("asyncThreadPoolExecutor")
    override fun sendNewArticleMessage(newArticles: List<ArticleMailDto>) {
        val message = WebHookMessage(buildNewArticleMessage(newArticles))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_NEW_ARTICLE_URL)
    }

    private fun buildNewArticleMessage(newArticles: List<ArticleMailDto>): String {
        val stringBuilder = StringBuilder()
        val prefix = """
            🔔 **새 게시글 알림**

            최근 24시간 동안 **${newArticles.size}개**의 새로운 게시글이 등록되었습니다!

            📅 ${LocalDateTime.now()}


        """.trimIndent()
        stringBuilder.append(prefix)

        for (article in newArticles) {
            stringBuilder.append("- [${article.companyNameKr}] -> ${article.title}").append("\n")
        }

        return stringBuilder.toString()
    }

    @Async("asyncThreadPoolExecutor")
    override fun sendNewInquiryMessage(newInquiry: Inquiry) {
        val message = WebHookMessage(buildNewInquiryMessage(newInquiry))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_NEW_INQUIRY_URL)
    }

    private fun buildNewInquiryMessage(newInquiry: Inquiry): String {
        val stringBuilder = StringBuilder()
        val prefix = """
            🔔 **새 문의하기 알림**
            
            📅 ${LocalDateTime.now()}


        """.trimIndent()
        stringBuilder.append(prefix)
        stringBuilder.append(newInquiry.content)
        return stringBuilder.toString()
    }


    @Async("asyncThreadPoolExecutor")
    override fun sendCategoryAllocateMessage(
        articles: List<ArticleMailDto>,
        categories: Map<Int, Category>
    ) {
        val message = WebHookMessage(buildCategoryAllocateMessage(articles, categories))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_CATEGORY_ALLOCATION_URL)
    }

    private fun buildCategoryAllocateMessage(
        articles: List<ArticleMailDto>,
        categories: Map<Int, Category>
    ): String {
        val stringBuilder = StringBuilder()
        val prefix = """
            🔔 **카테고리 할당 알림**
            
            📅 ${LocalDateTime.now()}


        """.trimIndent()
        stringBuilder.append(prefix)
        for (i in articles.indices) {
            val nowArticle = articles.get(i)
            val nowCategory = categories.get(i)
            stringBuilder.append("- [${nowArticle.companyNameKr}] ${nowArticle.title} -> ${nowCategory!!.name}")
                .append("\n")
        }
        return stringBuilder.toString()
    }
}