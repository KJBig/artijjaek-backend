package com.artijjaek.batch.crawler

import com.artijjaek.batch.crawler.blog.BlogCrawler
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CrawlerFactoryTest {

    @Test
    @DisplayName("등록된 블로그 이름으로 조회하면 해당 크롤러를 반환한다")
    fun getCrawlerTest() {
        // given
        val crawler = createCrawler("OLIVE YOUNG")
        val factory = CrawlerFactory(listOf(crawler))

        // when
        val result = factory.getCrawler("OLIVE YOUNG")

        // then
        assertThat(result).isSameAs(crawler)
    }

    @Test
    @DisplayName("등록되지 않은 블로그 이름으로 조회하면 예외가 발생한다")
    fun getCrawlerNotFoundTest() {
        // given
        val crawler = createCrawler("KAKAO")
        val factory = CrawlerFactory(listOf(crawler))

        // when
        val exception = assertThrows<IllegalArgumentException> {
            factory.getCrawler("UNKNOWN")
        }

        // then
        assertThat(exception.message).isEqualTo("Crawler not found for company: UNKNOWN")
    }

    private fun createCrawler(blogName: String): BlogCrawler {
        val crawler = mockk<BlogCrawler>()
        every { crawler.blogName } returns blogName
        every { crawler.crawl(any<Company>()) } returns emptyList<Article>()
        return crawler
    }
}
