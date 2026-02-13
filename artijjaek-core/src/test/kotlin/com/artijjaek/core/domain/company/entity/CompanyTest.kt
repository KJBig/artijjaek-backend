package com.artijjaek.core.domain.company.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test


class CompanyTest {

    @Test
    @DisplayName("회사의 크롤링 가능 여부를 변경할 수 있다")
    fun chaneCrawlAvailabilityTest() {
        // given
        val company = Company(
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = true
        )

        // when
        company.chaneCrawlAvailability(false)

        // then
        assertThat(company.crawlAvailability).isFalse
    }

}