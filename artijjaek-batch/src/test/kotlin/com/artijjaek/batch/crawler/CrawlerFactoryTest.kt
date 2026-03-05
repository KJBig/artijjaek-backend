package com.artijjaek.batch.crawler

import com.artijjaek.batch.crawler.patter.PatternCrawler
import com.artijjaek.batch.crawler.specific.CompanySpecificCrawler
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CrawlOrder
import com.artijjaek.core.domain.company.enums.CrawlPattern
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CrawlerFactoryTest {

    @Test
    @DisplayName("회사 전용 크롤러가 있으면 패턴보다 우선한다")
    fun getCrawler_companySpecificPriorityTest() {
        // given
        val patternCrawler = createPatternCrawler(CrawlPattern.RSS)
        val companyCrawler = createCompanyCrawler("DAANGN")
        val factory = CrawlerFactory(listOf(patternCrawler), listOf(companyCrawler))
        val company = createCompany(nameEn = "DAANGN", pattern = CrawlPattern.RSS)

        // when
        val result = factory.getCrawler(company)

        // then
        assertThat(result).isSameAs(companyCrawler)
    }

    @Test
    @DisplayName("회사 전용 크롤러가 없으면 패턴 크롤러를 반환한다")
    fun getCrawler_patternFallbackTest() {
        // given
        val patternCrawler = createPatternCrawler(CrawlPattern.RSS)
        val factory = CrawlerFactory(listOf(patternCrawler), emptyList())
        val company = createCompany(nameEn = "OLIVE YOUNG", pattern = CrawlPattern.RSS)

        // when
        val result = factory.getCrawler(company)

        // then
        assertThat(result).isSameAs(patternCrawler)
    }

    @Test
    @DisplayName("회사 전용/패턴 크롤러 모두 없으면 예외가 발생한다")
    fun getCrawler_notFoundTest() {
        // given
        val factory = CrawlerFactory(emptyList(), emptyList())
        val company = createCompany(nameEn = "UNKNOWN", pattern = CrawlPattern.RSS_ENTRY)

        // when
        val exception = assertThrows<IllegalArgumentException> {
            factory.getCrawler(company)
        }

        // then
        assertThat(exception.message).isEqualTo("Crawler not found for company: UNKNOWN, pattern: RSS_ENTRY")
    }

    private fun createPatternCrawler(pattern: CrawlPattern): PatternCrawler {
        val crawler = mockk<PatternCrawler>()
        every { crawler.pattern } returns pattern
        every { crawler.crawl(any<Company>()) } returns emptyList<Article>()
        return crawler
    }

    private fun createCompanyCrawler(companyNameEn: String): CompanySpecificCrawler {
        val crawler = mockk<CompanySpecificCrawler>()
        every { crawler.companyNameEn } returns companyNameEn
        every { crawler.crawl(any<Company>()) } returns emptyList<Article>()
        return crawler
    }

    private fun createCompany(nameEn: String, pattern: CrawlPattern): Company {
        return Company(
            nameKr = "테스트",
            nameEn = nameEn,
            logo = "logo",
            baseUrl = "https://example.com",
            blogUrl = "https://example.com/blog",
            crawlUrl = "/rss",
            crawlAvailability = true,
            crawlPattern = pattern,
            crawlOrder = CrawlOrder.NORMAL
        )
    }
}
