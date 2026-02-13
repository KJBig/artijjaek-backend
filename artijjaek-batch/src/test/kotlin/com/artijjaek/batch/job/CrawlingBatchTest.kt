package com.artijjaek.batch.job

import com.artijjaek.batch.config.TestConfig
import com.artijjaek.batch.crawler.CrawlerFactory
import com.artijjaek.batch.crawler.blog.BlogCrawler
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.company.entity.Company
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManagerFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [TestConfig::class])
@ActiveProfiles("test")
class CrawlingBatchTest {

    @Autowired
    lateinit var entityManagerFactory: EntityManagerFactory

    private val articleDomainService = mockk<ArticleDomainService>(relaxed = true)
    private val crawlerFactory = mockk<CrawlerFactory>()
    private val crawler = mockk<BlogCrawler>()

    private val config = CrawlingBatchConfig(
        mockk(),
        mockk(),
        mockk(),
        articleDomainService,
        crawlerFactory
    )

    @Test
    @DisplayName("회사 크롤링 리더는 crawlAvailability가 true인 회사만 읽어온다")
    fun crawlCompanyReaderTest() {
        // given
        val readerConfig = CrawlingBatchConfig(
            mockk(),
            mockk(),
            entityManagerFactory,
            articleDomainService,
            crawlerFactory
        )
        saveCompaniesForReaderTest(entityManagerFactory)
        val reader = readerConfig.crawlCompanyReader()
        val executionContext = ExecutionContext()

        // when
        reader.open(executionContext)
        val readCompanies = mutableListOf<Company>()
        var item = reader.read()
        while (item != null) {
            readCompanies.add(item)
            item = reader.read()
        }
        reader.close()

        // then
        assertThat(readCompanies).hasSize(2)
        assertThat(readCompanies.all { it.crawlAvailability }).isTrue()
        assertThat(readCompanies.map { it.nameEn })
            .containsExactlyInAnyOrder("OLIVE YOUNG", "KAKAO")
    }

    @Test
    @DisplayName("크롤링 프로세서는 중복된 기사를 제거하고 최신 순으로 반환한다")
    fun crawlingProcessorTest() {
        // given
        val company = createCompany()
        val article1 = createArticle(company, "아티클1", "url1")
        val article2 = createArticle(company, "아티클2", "url2")
        val article3 = createArticle(company, "아티클3", "url3")
        val processor = config.crawlingProcessor()

        every { crawlerFactory.getCrawler("OLIVE YOUNG") } returns crawler
        every { crawler.crawl(company) } returns listOf(article1, article2, article3)
        every { articleDomainService.findExistByUrls(company, any()) } returns listOf(article2)

        // when
        val result = processor.process(company)

        // then
        assertThat(result).hasSize(2)
        assertThat(result!![0].link).isEqualTo("url3")
        assertThat(result[1].link).isEqualTo("url1")
    }

    @Test
    @DisplayName("아티클 라이터는 전달된 기사 목록을 모두 저장한다")
    fun articleWriterTest() {
        // given
        val company = createCompany()
        val article1 = createArticle(company, "아티클1", "url1")
        val article2 = createArticle(company, "아티클2", "url2")
        val writer = config.articleWriter()

        // when
        writer.write(Chunk(listOf(listOf(article1), listOf(article2))))

        // then
        verify(exactly = 1) { articleDomainService.save(article1) }
        verify(exactly = 1) { articleDomainService.save(article2) }
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

    private fun saveCompaniesForReaderTest(entityManagerFactory: EntityManagerFactory) {
        val entityManager = entityManagerFactory.createEntityManager()
        entityManager.transaction.begin()
        entityManager.persist(
            Company(
                nameKr = "올리브영",
                nameEn = "OLIVE YOUNG",
                logo = "http://example.com/logo1.png",
                baseUrl = "http://example.com/1",
                blogUrl = "http://example.com/blog",
                crawlUrl = "http://example.com/crawl/1",
                crawlAvailability = true
            )
        )
        entityManager.persist(
            Company(
                nameKr = "크롤링불가회사",
                nameEn = "NO-CRAWL",
                logo = "http://example.com/logo2.png",
                baseUrl = "http://example.com/2",
                blogUrl = "http://example.com/blog",
                crawlUrl = "http://example.com/crawl/2",
                crawlAvailability = false
            )
        )
        entityManager.persist(
            Company(
                nameKr = "카카오",
                nameEn = "KAKAO",
                logo = "http://example.com/logo3.png",
                baseUrl = "http://example.com/3",
                blogUrl = "http://example.com/blog",
                crawlUrl = "http://example.com/crawl/3",
                crawlAvailability = true
            )
        )
        entityManager.transaction.commit()
        entityManager.close()
    }

}
