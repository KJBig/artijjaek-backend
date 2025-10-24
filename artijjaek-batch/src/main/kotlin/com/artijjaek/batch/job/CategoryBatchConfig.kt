package com.artijjaek.batch.job

import com.artijjaek.core.ai.GeminiClient
import com.artijjaek.core.domain.Article
import com.artijjaek.core.service.ArticleDomainService
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
    private val geminiClient: GeminiClient,
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
            .chunk<List<Article>, List<Article>>(10, transactionManager)
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
                    em.createQuery("SELECT a FROM Article a WHERE a.category is null", Article::class.java)
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
    fun allocateCategoryProcessor(): ItemProcessor<List<Article>, List<Article>> {
        return ItemProcessor { articles ->
            val categories = geminiClient.analyzeArticleCategory(articles)

            for (i in articles.indices) {
                val nowArticle = articles.get(i)
                val nowCategory = categories.get(i)
                nowArticle.changeCategory(nowCategory)
            }

            articles
        }

    }

    @Bean
    fun articleWriterForAllocateCategory(): ItemWriter<List<Article>> {
        return ItemWriter { items ->
            items.flatten().forEach { articleDomainService.save(it) }
        }
    }

}
