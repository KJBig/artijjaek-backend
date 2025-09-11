package com.noati.batch.job

import com.noati.batch.crawler.CrawlerFactory
import com.noati.core.ai.GeminiClient
import com.noati.core.domain.Article
import com.noati.core.domain.Company
import com.noati.core.service.ArticleDomainService
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
    private val geminiClient: GeminiClient,
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
            .reader(companyReader())
            .processor(crawlingProcessor())
            .writer(articleWriter())
            .build()
    }

    @Bean
    fun companyReader(): JpaPagingItemReader<Company> {
        return JpaPagingItemReaderBuilder<Company>()
            .name("companyReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT c FROM Company c")
            .pageSize(10)
            .build()
    }

    @Bean
    fun crawlingProcessor(): ItemProcessor<Company, List<Article>> {
        return ItemProcessor { company ->
            val crawler = crawlerFactory.getCrawler(company.nameEn)
            val crawledArticles = crawler.crawl(company)

            // Article 중복 제거
            val existingUrls = articleDomainService.findByCompanyRecent(company, 10)
                .map { it.link }
                .toList()
            val newArticles = crawledArticles.filter { it.link !in existingUrls }

            printDetectLog(company, newArticles)

            // 카테고리 분류
//            newArticles.forEach {
//                val articleCategory = geminiClient.analyzeArticleCategory(it.title, it.articleUrl)
//                it.changeCategory(articleCategory)
//            } 

            newArticles.reversed()
        }

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

}
