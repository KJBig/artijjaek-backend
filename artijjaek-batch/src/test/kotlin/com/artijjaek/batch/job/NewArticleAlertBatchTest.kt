package com.artijjaek.batch.job

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.webhook.WebHookService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.scope.context.StepContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.test.MetaDataInstanceFactory
import org.springframework.test.util.ReflectionTestUtils

class NewArticleAlertBatchTest {

    private val articleDomainService = mockk<ArticleDomainService>()
    private val webHookService = mockk<WebHookService>(relaxed = true)

    private val config = NewArticleAlertBatchConfig(
        mockk(),
        mockk(),
        articleDomainService,
        webHookService
    )

    @Test
    @DisplayName("디스코드 알림 스텝은 신규 게시글이 있으면 웹훅 알림을 전송한다")
    fun sendDiscordNotificationStepTest() {
        // given
        val company = createCompany()
        val article = createArticle(company, "아티클1", "url1")
        val step = config.sendDiscordNotificationStep(mockk(), mockk())
        val tasklet = ReflectionTestUtils.getField(step, "tasklet") as Tasklet
        val stepExecution = MetaDataInstanceFactory.createStepExecution()
        val contribution = StepContribution(stepExecution)
        val chunkContext = ChunkContext(StepContext(stepExecution))

        every { articleDomainService.findTodayArticle() } returns listOf(article)

        // when
        val result = tasklet.execute(contribution, chunkContext)

        // then
        assertThat(result).isEqualTo(RepeatStatus.FINISHED)
        verify(exactly = 1) { webHookService.sendNewArticleMessage(any()) }
    }

    private fun createCompany(): Company {
        return Company(
            nameKr = "올리브영",
            nameEn = "OLIVE YOUNG",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )
    }

    private fun createArticle(company: Company, title: String, link: String): Article {
        return Article(
            title = title,
            link = link,
            company = company,
            category = null,
            description = null,
            image = null
        )
    }
}
