package com.artijjaek.batch.job

import com.artijjaek.batch.service.RobotsTxtCheckService
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.item.Chunk

class RobotTxtBatchTest {

    private val companyDomainService = mockk<CompanyDomainService>(relaxed = true)
    private val robotsTxtCheckService = mockk<RobotsTxtCheckService>()

    private val config = RobotTxtBatchConfig(
        mockk(),
        mockk(),
        mockk(),
        companyDomainService,
        robotsTxtCheckService
    )

    @Test
    @DisplayName("로봇 텍스트 리더를 생성하면 robotTextCompanyReader 이름으로 생성된다")
    fun robotTextCompanyReaderTest() {
        // given

        // when
        val reader = config.robotTextCompanyReader()

        // then
        assertThat(reader.name).isEqualTo("robotTextCompanyReader")
    }

    @Test
    @DisplayName("로봇 텍스트 프로세서는 크롤링 불가 회사의 crawlAvailability를 false로 변경한다")
    fun checkRobotTxtProcessorTest() {
        // given
        val company = createCompany(crawlAvailability = true)
        val processor = config.checkRobotTxtProcessor()
        every { robotsTxtCheckService.isCrawlAllowed(company) } returns false

        // when
        val result = processor.process(company)

        // then
        assertThat(result).isNotNull
        assertThat(result!!.crawlAvailability).isFalse()
    }

    @Test
    @DisplayName("회사 라이터는 전달된 회사를 모두 저장한다")
    fun companyWriterTest() {
        // given
        val company1 = createCompany(crawlAvailability = true)
        val company2 = createCompany(crawlAvailability = false)
        val writer = config.companyWriter()

        // when
        writer.write(Chunk(listOf(company1, company2)))

        // then
        verify(exactly = 1) { companyDomainService.save(company1) }
        verify(exactly = 1) { companyDomainService.save(company2) }
    }

    private fun createCompany(crawlAvailability: Boolean): Company {
        return Company(
            nameKr = "올리브영",
            nameEn = "OLIVE YOUNG",
            logo = "http://example.com/logo.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog",
            crawlUrl = "http://example.com/crawl",
            crawlAvailability = crawlAvailability
        )
    }
}
