package com.artijjaek.core.domain.article.repository

import com.artijjaek.core.config.TestConfig
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.category.repository.CategoryRepository
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.repository.CompanyRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime

@DataJpaTest
@ContextConfiguration(classes = [TestConfig::class])
@ActiveProfiles("test")
class ArticleRepositoryTest {

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var companyRepository: CompanyRepository

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var tem: TestEntityManager

    @Autowired
    lateinit var em: EntityManager


    @AfterEach
    fun clear() {
        articleRepository.deleteAll()
        companyRepository.deleteAll()
    }

    private fun changeArticleCreatedAt(articleId: Long, time: LocalDateTime) {
        tem.entityManager.createNativeQuery(
            "update article set created_at = :t where article_id = :id"
        )
            .setParameter("t", time)
            .setParameter("id", articleId)
            .executeUpdate()

        tem.flush()
        tem.clear()
    }

    @Test
    @DisplayName("회사를 기준으로 가장 최근에 생성된 아티클을 조회한다")
    fun findByCompanyRecentTest() {
        // given
        val company = Company(
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )

        val saveCompany = companyRepository.save(company)


        val articles = ArrayList<Article>();

        for (i in 1..5) {
            val article = Article(
                title = "아티클${i}",
                link = "http://example.com/article${i}",
                company = saveCompany,
                category = null,
                description = null,
                image = null
            )

            articles.add(article)
        }

        articleRepository.saveAll(articles)

        val lateArticle = Article(
            title = "아티클6",
            link = "http://example.com/article6",
            company = company,
            category = null,
            description = null,
            image = null
        )
        articleRepository.save(lateArticle)

        // when
        val result = articleRepository.findByCompanyRecent(company, 5L)


        // then
        assertThat(result.size).isEqualTo(5)
        assertThat(result[0].title).isEqualTo("아티클6")
    }

    @Test
    @DisplayName("오늘 생성된 아티클을 조회한다")
    fun findTodayArticleTest() {
        // given
        val company = Company(
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )
        val saveCompany = companyRepository.save(company)

        val articles = ArrayList<Article>();
        for (i in 1..5) {
            val article = Article(
                title = "아티클${i}",
                link = "http://example.com/article${i}",
                company = saveCompany,
                category = null,
                description = null,
                image = null
            )
            articles.add(article)
        }
        articleRepository.saveAll(articles)

        val oldArticle = Article(
            title = "아티클6",
            link = "http://example.com/article6",
            company = company,
            category = null,
            description = null,
            image = null
        )
        val saveOldArticle = articleRepository.save(oldArticle)

        changeArticleCreatedAt(saveOldArticle.id!!, LocalDateTime.now().minusDays(5))

        // when
        val result = articleRepository.findTodayArticle()


        // then
        assertThat(result.size).isEqualTo(5)
        assertThat(result[0].title).isEqualTo("아티클5")
    }


    @Test
    @DisplayName("회사와 카테고리를 기준으로 오늘 생성된 아티클을 조회한다")
    fun findTodayByCompaniesAndCategoriesTest() {
        // given
        val company = Company(
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )
        val saveCompany = companyRepository.save(company)

        val backCategory = Category(
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )

        val frontCategory = Category(
            name = "프론트",
            publishType = PublishType.PUBLISH
        )
        val saveBackCategory = categoryRepository.save(backCategory)
        val saveFrontCategory = categoryRepository.save(frontCategory)


        val articles = ArrayList<Article>();
        for (i in 1..5) {
            val article = Article(
                title = "아티클${i}",
                link = "http://example.com/article${i}",
                company = saveCompany,
                category = saveBackCategory,
                description = null,
                image = null
            )

            articles.add(article)
        }

        articleRepository.saveAll(articles)

        val frontArticle = Article(
            title = "아티클6",
            link = "http://example.com/article6",
            company = company,
            category = saveFrontCategory,
            description = null,
            image = null
        )
        articleRepository.save(frontArticle)

        // when
        val result = articleRepository.findTodayByCompaniesAndCategories(
            listOf(saveCompany),
            listOf(backCategory)
        )


        // then
        assertThat(result.size).isEqualTo(5)
        assertThat(result[0].title).isEqualTo("아티클5")
    }

    @Test
    @DisplayName("아티클에 카테고리를 할당한다")
    fun allocateCategoryTest() {
        // given
        val company = Company(
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )
        val saveCompany = companyRepository.save(company)

        val category = Category(
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val saveCategory = categoryRepository.save(category)

        val article = Article(
            title = "아티클",
            link = "http://example.com/article",
            company = saveCompany,
            category = null,
            description = null,
            image = null
        )
        val saveArticle = articleRepository.save(article)

        // when
        articleRepository.allocateCategory(saveArticle, saveCategory)
        em.flush()
        em.clear()


        // then
        val findArticle = articleRepository.findById(saveArticle.id!!).orElse(null)!!
        assertThat(findArticle.category).isNotNull
        assertThat(findArticle.category!!.name).isEqualTo("백엔드")
    }

    @Test
    @DisplayName("회사와 아티클 URL 목록을 기준으로 존재하는 아티클을 조회한다")
    fun findExistByUrlsTest() {
        // given
        val company = Company(
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )
        val saveCompany = companyRepository.save(company)

        val article = Article(
            title = "아티클",
            link = "http://example.com/article",
            company = saveCompany,
            category = null,
            description = null,
            image = null
        )
        articleRepository.save(article)

        // when
        val result = articleRepository.findExistByUrls(saveCompany, listOf("http://example.com/article"))


        // then
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].title).isEqualTo("아티클")
    }
}