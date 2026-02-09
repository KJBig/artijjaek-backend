package com.artijjaek.api.service

import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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
        val pageRequest = PageRequest.of(0, 1)

        val company = Company(
            id = 1L,
            nameKr = "회사1",
            nameEn = "Company1",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )

        val companyPage = PageImpl(
            listOf(company),
            pageRequest,
            1
        )

        every { companyDomainService.findWithPageable(pageRequest) }.returns(companyPage)


        // when
        val response = companyService.searchCompanyList(pageRequest)

        // then
        assertThat(response.pageNumber).isEqualTo(0)
        assertThat(response.hasNext).isEqualTo(false)
        assertThat(response.content.size).isEqualTo(1)
        assertThat(response.content.get(0).companyId).isEqualTo(1L)
    }

}