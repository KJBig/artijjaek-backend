package com.artijjaek.batch.job

import com.artijjaek.batch.crawler.CrawlerFactory
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CrawlOrder
import com.artijjaek.core.webhook.WebHookService
import jakarta.persistence.EntityManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class CrawlingBatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val articleDomainService: ArticleDomainService,
    private val crawlerFactory: CrawlerFactory,
    private val webHookService: WebHookService,
) {

    private val log = LoggerFactory.getLogger(CrawlingBatchConfig::class.java)

    @Bean
    fun crawlingJob(): Job {
        return JobBuilder("crawlingJob", jobRepository)
            .start(crawlingStep())
            .build()
    }

    @Bean
    fun crawlingStep(): Step {
        return StepBuilder("crawlingStep", jobRepository)
            .chunk<Company, List<Article>>(10, transactionManager)
            .reader(crawlCompanyReader())
            .processor(crawlingProcessor())
            .writer(articleWriter())
            .build()
    }

    @Bean
    fun crawlCompanyReader(): JpaPagingItemReader<Company> {
        return JpaPagingItemReaderBuilder<Company>()
            .name("crawlCompanyReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT c FROM Company c WHERE c.crawlAvailability = true")
            .pageSize(10)
            .build()
    }

    @Bean
    fun crawlingProcessor(): ItemProcessor<Company, List<Article>> {
        return ItemProcessor { company ->
            processWithRetry(company)
        }

    }

    private fun processWithRetry(company: Company): List<Article> {
        var lastException: Exception? = null

        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                return processOnce(company)
            } catch (e: Exception) {
                lastException = e
                log.warn(
                    "[{}] 크롤링 시도 실패 (attempt {}/{}): {}",
                    company.nameKr,
                    attempt + 1,
                    MAX_RETRY_ATTEMPTS,
                    e.message
                )
            }
        }

        log.error("[{}] 크롤링 최종 실패. 회사 스킵 처리", company.nameKr, lastException)
        try {
            webHookService.sendCrawlErrorMessage(
                companyNameKr = company.nameKr,
                errorMessage = lastException?.message
            )
        } catch (webhookException: Exception) {
            log.error("[{}] 크롤링 실패 Discord 전송도 실패: {}", company.nameKr, webhookException.message)
        }

        return emptyList()
    }

    private fun processOnce(company: Company): List<Article> {
        val crawler = crawlerFactory.getCrawler(company)
        val crawledArticles = applyCrawlOrderAndLimit(crawler.crawl(company), company.crawlOrder)

        // Article 중복 제거
        val crawledArticleUrls = crawledArticles.map { it.link }.toList()
        val existingUrls = articleDomainService.findExistByUrls(company, crawledArticleUrls)
            .map { it.link }
            .toList()

        val newArticles = crawledArticles.filter { it.link !in existingUrls }

        printDetectLog(company, newArticles)

        return newArticles.reversed()
    }

    private fun applyCrawlOrderAndLimit(articles: List<Article>, crawlOrder: CrawlOrder): List<Article> {
        val ordered = when (crawlOrder) {
            CrawlOrder.NORMAL -> articles
            CrawlOrder.REVERSE -> articles.reversed()
        }
        return ordered.take(10)
    }

    private fun printDetectLog(company: Company, newArticles: List<Article>) {
        log.info("[${company.nameKr}] 새로 발견된 글의 수 : ${newArticles.size}")
        for (article in newArticles) {
            log.info("[NEW ARTICLE] (company: ${article.company.nameKr}, title: ${article.title}, url: ${article.link})")
        }
    }

    @Bean
    fun articleWriter(): ItemWriter<List<Article>> {
        return ItemWriter { items ->
            items.flatten().forEach { articleDomainService.save(it) }
        }
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 2
    }

}
