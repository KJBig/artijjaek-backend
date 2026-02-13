package com.artijjaek.api.service

import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CompanySortOption
import com.artijjaek.core.domain.company.service.CompanyDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CompanyServiceTest {

    @InjectMockKs
    lateinit var companyService: CompanyService

    @MockK
    lateinit var companyDomainService: CompanyDomainService

    @Test
    @DisplayName("회사 목록을 조회할 수 있다")
    fun searchCompanyListTest() {
        // given
        val sortOption = CompanySortOption.KR_NAME
        val pageRequest = PageRequest.of(0, 1)

        val company = Company(
            id = 1L,
            nameKr = "회사1",
            nameEn = "Company1",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog1",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )

        val companyPage = PageImpl(
            listOf(company),
            pageRequest,
            1
        )

        every { companyDomainService.findWithPageableOrderBySortOption(sortOption, pageRequest) }.returns(companyPage)


        // when
        val response = companyService.searchCompanyList(sortOption, pageRequest)

        // then
        assertThat(response.pageNumber).isEqualTo(0)
        assertThat(response.hasNext).isEqualTo(false)
        assertThat(response.content.size).isEqualTo(1)
        assertThat(response.content.get(0).companyId).isEqualTo(1L)
        verify(exactly = 1) { companyDomainService.findWithPageableOrderBySortOption(sortOption, pageRequest) }
    }

    @Test
    @DisplayName("회사 목록 조회 시 POPULARITY 정렬 옵션을 전달할 수 있다")
    fun searchCompanyListTest_SortOptionPopularity() {
        // given
        val sortOption = CompanySortOption.POPULARITY
        val pageRequest = PageRequest.of(0, 2)

        val firstCompany = Company(
            id = 1L,
            nameKr = "인기회사",
            nameEn = "PopularCompany",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog1",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )
        val secondCompany = Company(
            id = 2L,
            nameKr = "일반회사",
            nameEn = "NormalCompany",
            logo = "http://example.com/logo2.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog2",
            crawlUrl = "http://example.com/crawl2",
            crawlAvailability = true
        )

        every {
            companyDomainService.findWithPageableOrderBySortOption(sortOption, pageRequest)
        }.returns(PageImpl(listOf(firstCompany, secondCompany), pageRequest, 2))

        // when
        val response = companyService.searchCompanyList(sortOption, pageRequest)

        // then
        assertThat(response.content).hasSize(2)
        assertThat(response.content[0].companyId).isEqualTo(1L)
        verify(exactly = 1) { companyDomainService.findWithPageableOrderBySortOption(sortOption, pageRequest) }
    }

}
