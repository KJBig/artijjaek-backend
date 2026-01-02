package com.artijjaek.core.webhook

import com.artijjaek.core.domain.article.entity.Article
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


    @Async("asyncThreadPoolExecutor")
    override fun sendNewArticleMessage(newArticles: List<Article>) {
        val message = WebHookMessage(buildNewArticleMessage(newArticles))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_NEW_ARTICLE_URL)
    }

    private fun buildNewArticleMessage(newArticles: List<Article>): String {
        val stringBuilder = StringBuilder()
        val prefix = """
            🔔 **새 게시글 알림**

            최근 24시간 동안 **${newArticles.size}개**의 새로운 게시글이 등록되었습니다!

            📅 ${LocalDateTime.now()}


        """.trimIndent()
        stringBuilder.append(prefix)

        for (article in newArticles) {
            stringBuilder.append("- [${article.company.nameKr}] -> ${article.title}").append("\n")
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

}