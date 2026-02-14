package com.artijjaek.batch.job

import com.artijjaek.batch.dto.ArticleCategory
import com.artijjaek.core.ai.GeminiClient
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.webhook.WebHookService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.TypedQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.item.Chunk

class CategoryBatchTest {

    private val entityManagerFactory = mockk<EntityManagerFactory>()
    private val articleDomainService = mockk<ArticleDomainService>(relaxed = true)
    private val categoryDomainService = mockk<CategoryDomainService>()
    private val geminiClient = mockk<GeminiClient>()
    private val webHookService = mockk<WebHookService>(relaxed = true)

    private val config = CategoryBatchConfig(
        mockk(),
        mockk(),
        entityManagerFactory,
        articleDomainService,
        categoryDomainService,
        geminiClient,
        webHookService
    )

    @Test
    @DisplayName("카테고리 할당 리더는 카테고리가 없는 게시글을 페이지 단위로 조회한다")
    fun articleReaderForAllocateCategoryTest() {
        // given
        val company = createCompany()
        val article = createArticle(company, "아티클1", "url1")
        val entityManager = mockk<EntityManager>()
        val query = mockk<TypedQuery<Article>>()
        val reader = config.articleReaderForAllocateCategory()

        every { entityManagerFactory.createEntityManager() } returns entityManager
        every { entityManager.close() } returns Unit
        every {
            entityManager.createQuery(
                "SELECT a FROM Article a LEFT JOIN FETCH a.company WHERE a.category IS NULL ORDER BY a.id DESC",
                Article::class.java
            )
        } returns query
        every { query.setFirstResult(any()) } returns query
        every { query.setMaxResults(any()) } returns query
        every { query.resultList } returnsMany listOf(listOf(article), emptyList())

        // when
        val first = reader.read()
        val second = reader.read()

        // then
        assertThat(first).hasSize(1)
        assertThat(first!![0].link).isEqualTo("url1")
        assertThat(second).isNull()
    }

    @Test
    @DisplayName("카테고리 할당 프로세서는 분석 결과를 ArticleCategory 목록으로 반환한다")
    fun allocateCategoryProcessorTest() {
        // given
        val company = createCompany()
        val article1 = createArticle(company, "아티클1", "url1")
        val article2 = createArticle(company, "아티클2", "url2")
        val category1 = Category(name = "백엔드", publishType = PublishType.PUBLISH)
        val category2 = Category(name = "프론트엔드", publishType = PublishType.PUBLISH)
        val categories = listOf(category1, category2)
        val processor = config.allocateCategoryProcessor()

        every { categoryDomainService.findAll() } returns categories
        every { geminiClient.analyzeArticleCategory(listOf(article1, article2), categories) } returns mapOf(
            0 to category1,
            1 to category2
        )

        // when
        val result = processor.process(listOf(article1, article2))

        // then
        assertThat(result).hasSize(2)
        assertThat(result!![0]).isEqualTo(ArticleCategory(article1, category1))
        assertThat(result[1]).isEqualTo(ArticleCategory(article2, category2))
        verify(exactly = 1) { webHookService.sendCategoryAllocateMessage(any(), mapOf(0 to category1, 1 to category2)) }
    }

    @Test
    @DisplayName("카테고리 할당 라이터는 전달된 ArticleCategory를 모두 반영한다")
    fun articleWriterForAllocateCategoryTest() {
        // given
        val company = createCompany()
        val article1 = createArticle(company, "아티클1", "url1")
        val article2 = createArticle(company, "아티클2", "url2")
        val category1 = Category(name = "백엔드", publishType = PublishType.PUBLISH)
        val category2 = Category(name = "프론트엔드", publishType = PublishType.PUBLISH)
        val writer = config.articleWriterForAllocateCategory()

        // when
        writer.write(
            Chunk(
                listOf(
                    listOf(ArticleCategory(article1, category1)),
                    listOf(ArticleCategory(article2, category2))
                )
            )
        )

        // then
        verify(exactly = 1) { articleDomainService.allocateCategory(article1, category1) }
        verify(exactly = 1) { articleDomainService.allocateCategory(article2, category2) }
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
