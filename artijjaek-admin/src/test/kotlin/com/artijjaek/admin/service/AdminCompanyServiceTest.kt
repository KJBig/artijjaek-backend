package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostCompanyRequest
import com.artijjaek.admin.dto.request.PutCompanyRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CrawlOrder
import com.artijjaek.core.domain.company.enums.CrawlPattern
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.subscription.dto.TopSubscribedCompanyCount
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AdminCompanyServiceTest {

    @InjectMockKs
    lateinit var adminCompanyService: AdminCompanyService

    @MockK
    lateinit var companyDomainService: CompanyDomainService

    @MockK
    lateinit var companySubscriptionDomainService: CompanySubscriptionDomainService

    @Test
    @DisplayName("회원 편집 드롭다운 회사 옵션을 조회한다")
    fun getMemberCompanyOptionsTest() {
        // given
        val companyA = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "https://cdn.example.com/company-a.png",
            baseUrl = "base-a",
            blogUrl = "blog-a",
            crawlUrl = "crawl-a",
            crawlAvailability = true
        )
        val companyB = Company(
            id = 11L,
            nameKr = "회사B",
            nameEn = "CompanyB",
            logo = "https://cdn.example.com/company-b.png",
            baseUrl = "base-b",
            blogUrl = "blog-b",
            crawlUrl = "crawl-b",
            crawlAvailability = true
        )
        every { companyDomainService.findAll() } returns listOf(companyA, companyB)

        // when
        val result = adminCompanyService.getMemberCompanyOptions()

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].companyId).isEqualTo(10L)
        assertThat(result[0].logo).isEqualTo("https://cdn.example.com/company-a.png")
    }

    @Test
    @DisplayName("구독자들이 많이 구독한 회사 Top 목록을 동점 포함으로 조회한다")
    fun getTopSubscribedCompaniesTest() {
        // given
        every { companySubscriptionDomainService.findTopSubscribedCompaniesWithTies(5) } returns listOf(
            TopSubscribedCompanyCount(companyId = 1L, companyNameKr = "A회사", subscriberCount = 10),
            TopSubscribedCompanyCount(companyId = 2L, companyNameKr = "B회사", subscriberCount = 8),
            TopSubscribedCompanyCount(companyId = 3L, companyNameKr = "C회사", subscriberCount = 8),
            TopSubscribedCompanyCount(companyId = 4L, companyNameKr = "D회사", subscriberCount = 7),
        )

        // when
        val result = adminCompanyService.getTopSubscribedCompanies()

        // then
        assertThat(result).hasSize(4)
        assertThat(result[0].rank).isEqualTo(1)
        assertThat(result[1].rank).isEqualTo(2)
        assertThat(result[2].rank).isEqualTo(2)
        assertThat(result[3].rank).isEqualTo(4)
    }

    @Test
    @DisplayName("회사 리스트를 한글/영문 이름 키워드로 조회한다")
    fun searchCompaniesTest() {
        // given
        val pageable = PageRequest.of(0, 10)
        val company = Company(
            id = 1L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo",
            baseUrl = "base",
            blogUrl = "blog",
            crawlUrl = "crawl",
            crawlAvailability = true,
            crawlPattern = CrawlPattern.RSS,
            crawlOrder = CrawlOrder.NORMAL
        )
        every { companyDomainService.findWithCondition(pageable, "회사") } returns PageImpl(listOf(company), pageable, 1)

        // when
        val result = adminCompanyService.searchCompanies(pageable, "회사")

        // then
        assertThat(result.totalCount).isEqualTo(1L)
        assertThat(result.content[0].companyId).isEqualTo(1L)
        assertThat(result.content[0].nameEn).isEqualTo("CompanyA")
    }

    @Test
    @DisplayName("회사를 등록한다")
    fun createCompanyTest() {
        // given
        val request = PostCompanyRequest(
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo",
            baseUrl = "base",
            blogUrl = "blog",
            crawlUrl = "crawl",
            crawlAvailability = true,
            crawlPattern = CrawlPattern.RSS,
            crawlOrder = CrawlOrder.NORMAL
        )
        every { companyDomainService.save(any()) } answers {
            val company = firstArg<Company>()
            company.id = 100L
        }

        // when
        val result = adminCompanyService.createCompany(request)

        // then
        assertThat(result.companyId).isEqualTo(100L)
    }

    @Test
    @DisplayName("회사 정보를 수정한다")
    fun updateCompanyTest() {
        // given
        val company = Company(
            id = 1L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo",
            baseUrl = "base",
            blogUrl = "blog",
            crawlUrl = "crawl",
            crawlAvailability = true,
            crawlPattern = CrawlPattern.RSS,
            crawlOrder = CrawlOrder.NORMAL
        )
        val request = PutCompanyRequest(
            nameKr = "회사B",
            nameEn = "CompanyB",
            logo = "logo2",
            baseUrl = "base2",
            blogUrl = "blog2",
            crawlUrl = "crawl2",
            crawlAvailability = false,
            crawlPattern = CrawlPattern.RSS_ENTRY,
            crawlOrder = CrawlOrder.REVERSE
        )
        every { companyDomainService.findById(1L) } returns company
        every { companyDomainService.save(company) } just runs

        // when
        adminCompanyService.updateCompany(1L, request)

        // then
        assertThat(company.nameKr).isEqualTo("회사B")
        assertThat(company.nameEn).isEqualTo("CompanyB")
        assertThat(company.crawlPattern).isEqualTo(CrawlPattern.RSS_ENTRY)
        assertThat(company.crawlOrder).isEqualTo(CrawlOrder.REVERSE)
        verify(exactly = 1) { companyDomainService.save(company) }
    }

    @Test
    @DisplayName("수정 대상 회사가 없으면 예외가 발생한다")
    fun updateCompanyNotFoundTest() {
        // given
        val request = PutCompanyRequest(
            nameKr = "회사B",
            nameEn = "CompanyB",
            logo = "logo2",
            baseUrl = "base2",
            blogUrl = "blog2",
            crawlUrl = "crawl2",
            crawlAvailability = false,
            crawlPattern = CrawlPattern.RSS_ENTRY,
            crawlOrder = CrawlOrder.REVERSE
        )
        every { companyDomainService.findById(999L) } returns null

        // when
        assertThrows<ApplicationException> {
            adminCompanyService.updateCompany(999L, request)
        }
    }
}
