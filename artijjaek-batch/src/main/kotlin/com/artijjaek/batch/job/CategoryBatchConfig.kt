package com.artijjaek.batch.job

import com.artijjaek.batch.dto.ArticleCategory
import com.artijjaek.core.ai.GeminiClient
import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.webhook.WebHookService
import jakarta.persistence.EntityManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class CategoryBatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val articleDomainService: ArticleDomainService,
    private val categoryDomainService: CategoryDomainService,
    private val geminiClient: GeminiClient,
    private val webHookService: WebHookService,
) {

    private val log = LoggerFactory.getLogger(CategoryBatchConfig::class.java)

    @Bean
    fun allocateCategoryJob(): Job {
        return JobBuilder("allocateCategoryJob", jobRepository)
            .start(allocateCategoryStep())
            .build()
    }

    @Bean
    fun allocateCategoryStep(): Step {
        return StepBuilder("allocateCategoryStep", jobRepository)
            .chunk<List<Article>, List<ArticleCategory>>(10, transactionManager)
            .reader(articleReaderForAllocateCategory())
            .processor(allocateCategoryProcessor())
            .writer(articleWriterForAllocateCategory())
            .build()
    }

    @Bean
    fun articleReaderForAllocateCategory(): ItemReader<List<Article>> {
        return object : ItemReader<List<Article>> {
            private val pageSize = 10
            private var currentPage = 0

            @Synchronized
            override fun read(): List<Article>? {
                val query = entityManagerFactory.createEntityManager().use { em ->
                    em.createQuery(
                        "SELECT a FROM Article a LEFT JOIN FETCH a.company WHERE a.category IS NULL ORDER BY a.id DESC",
                        Article::class.java
                    )
                        .setFirstResult(currentPage * pageSize)
                        .setMaxResults(pageSize)
                        .resultList
                }

                return if (query.isEmpty()) {
                    null // 더 이상 읽을 데이터 없음
                } else {
                    currentPage++
                    query
                }
            }
        }
    }

    @Bean
    fun allocateCategoryProcessor(): ItemProcessor<List<Article>, List<ArticleCategory>> {
        return ItemProcessor { articles ->
            val articleCategories = ArrayList<ArticleCategory>()
            val categories = categoryDomainService.findAll()
            val categoryMap = geminiClient.analyzeArticleCategory(articles, categories)

            if (articles.isEmpty()) {
                log.info("No Article")
            }

            for (i in articles.indices) {
                val nowArticle = articles.get(i)
                categoryMap[i]?.let { nowCategory ->
                    log.info("${nowArticle.title} : ${nowCategory.name}")
                    articleCategories.add(ArticleCategory(nowArticle, nowCategory))
                }
            }

            val articleDatas = articles.map { ArticleAlertDto.from(it) }.toList()

            webHookService.sendCategoryAllocateMessage(articleDatas, categoryMap)

            articleCategories

        }

    }

    @Bean
    fun articleWriterForAllocateCategory(): ItemWriter<List<ArticleCategory>> {
        return ItemWriter { items ->
            items.flatten().forEach { articleDomainService.allocateCategory(it.article, it.category) }
        }
    }

}
