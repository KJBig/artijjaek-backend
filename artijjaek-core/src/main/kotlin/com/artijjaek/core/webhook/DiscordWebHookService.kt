package com.artijjaek.core.webhook

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.inquiry.entity.Inquiry
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.unsubscription.entity.Unsubscription
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

    @Value("\${discord.webhook.new-subscribe}")
    lateinit var DISCORD_NEW_SUBSCRIBE_URL: String;

    @Value("\${discord.webhook.unsubscribe}")
    lateinit var DISCORD_UNSUBSCRIBE_URL: String;

    @Value("\${discord.webhook.mail-error}")
    lateinit var DISCORD_MAIL_ERROR_URL: String;

    @Value("\${discord.webhook.crawl-error}")
    lateinit var DISCORD_CRAWL_ERROR_URL: String;


    @Async("asyncThreadPoolExecutor")
    override fun sendNewArticleMessage(newArticles: List<ArticleAlertDto>) {
        val message = WebHookMessage(buildNewArticleMessage(newArticles))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_NEW_ARTICLE_URL)
    }

    private fun buildNewArticleMessage(newArticles: List<ArticleAlertDto>): String {
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
        articles: List<ArticleAlertDto>,
        categories: Map<Int, Category>
    ) {
        val message = WebHookMessage(buildCategoryAllocateMessage(articles, categories))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_CATEGORY_ALLOCATION_URL)
    }

    private fun buildCategoryAllocateMessage(
        articles: List<ArticleAlertDto>,
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

    @Async("asyncThreadPoolExecutor")
    override fun sendNewSubscribeMessage(newMember: Member) {
        val message = WebHookMessage(buildNewSubscribeMessage(newMember))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_NEW_SUBSCRIBE_URL)
    }

    private fun buildNewSubscribeMessage(member: Member): String {
        val stringBuilder = StringBuilder()
        val prefix = """
            🔔 **새 구독 알림**
            
            📅 ${LocalDateTime.now()}


        """.trimIndent()
        stringBuilder.append(prefix)
        stringBuilder.append("Email : ").append(member.email).append("\n")
        stringBuilder.append("Nickname : ").append(member.nickname).append("\n")
        return stringBuilder.toString()
    }

    @Async("asyncThreadPoolExecutor")
    override fun sendUnsubscribeMessage(member: Member, unsubscription: Unsubscription) {
        val message = WebHookMessage(buildUnsubscribeMessage(member, unsubscription))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_UNSUBSCRIBE_URL)
    }

    private fun buildUnsubscribeMessage(member: Member, unsubscription: Unsubscription): String {
        val stringBuilder = StringBuilder()
        val prefix = """
            🔔 **구독 해지 알림**
            
            📅 ${LocalDateTime.now()}


        """.trimIndent()
        stringBuilder.append(prefix)
        stringBuilder.append("Email : ").append(member.email).append("\n")
        stringBuilder.append("Nickname : ").append(member.nickname).append("\n")
        stringBuilder.append("Reason : ").append(unsubscription.reason).append("\n")
        stringBuilder.append("Detail : ").append(unsubscription.detail).append("\n")
        return stringBuilder.toString()
    }

    @Async("asyncThreadPoolExecutor")
    override fun sendMailErrorMessage(outboxId: Long?, errorMessage: String?) {
        val message = WebHookMessage(buildMailErrorMessage(outboxId, errorMessage))
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_MAIL_ERROR_URL)
    }

    @Async("asyncThreadPoolExecutor")
    override fun sendMailAlertMessage(content: String) {
        val message = WebHookMessage(content)
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_MAIL_ERROR_URL)
    }

    @Async("asyncThreadPoolExecutor")
    override fun sendCrawlErrorMessage(companyNameKr: String, errorMessage: String?) {
        val message = WebHookMessage(
            buildCrawlErrorMessage(
                companyNameKr = companyNameKr,
                errorMessage = errorMessage
            )
        )
        discordWebHookConnector.sendMessageForDiscord(message, DISCORD_CRAWL_ERROR_URL)
    }

    private fun buildMailErrorMessage(outboxId: Long?, errorMessage: String?): String {
        val stringBuilder = StringBuilder()
        val prefix = """
            🚨 **메일 발송 DEAD 발생**
            
            📅 ${LocalDateTime.now()}


        """.trimIndent()
        stringBuilder.append(prefix)
        stringBuilder.append("Outbox Id : ").append(outboxId).append("\n")
        stringBuilder.append("Error : ").append(errorMessage ?: "unknown").append("\n")
        return stringBuilder.toString()
    }

    private fun buildCrawlErrorMessage(companyNameKr: String, errorMessage: String?): String {
        return """
            🚨 **크롤링 재시도 실패**

            📅 ${LocalDateTime.now()}

            Company : $companyNameKr
            Error : ${errorMessage ?: "unknown"}
        """.trimIndent()
    }
}
