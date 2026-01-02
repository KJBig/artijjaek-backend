package com.artijjaek.batch.job

import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.webhook.WebHookService
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class NewArticleAlertBatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val articleDomainService: ArticleDomainService,
    private val webHookService: WebHookService,
) {

    private val log = LoggerFactory.getLogger(CategoryBatchConfig::class.java)

    @Bean
    fun newArticleAlertJob(): Job {
        return JobBuilder("newArticleAlertJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(sendDiscordNotificationStep(jobRepository, transactionManager))
            .build()
    }

    @Bean
    fun sendDiscordNotificationStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("sendDiscordNotificationStep", jobRepository)
            .tasklet({ contribution, chunkContext ->
                try {
                    // 최근 24시간 이내 새 게시글 조회
                    val newArticles = articleDomainService.findYesterdayArticle()

                    if (newArticles.isEmpty()) {
                        log.info("새로운 게시글이 없습니다.")
                        return@tasklet RepeatStatus.FINISHED
                    }

                    // 디스코드로 알림 전송
                    webHookService.sendNewArticleMessage(newArticles)

                    log.info("디스코드 알림 전송 완료: ${newArticles.size}개의 새 게시글")

                } catch (e: Exception) {
                    log.info("디스코드 알림 전송 실패: ${e.message}")
                    throw e
                }

                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }


}