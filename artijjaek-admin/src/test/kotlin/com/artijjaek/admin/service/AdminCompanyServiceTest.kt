package com.artijjaek.admin.service

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
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AdminCompanyServiceTest {

    @InjectMockKs
    lateinit var adminCompanyService: AdminCompanyService

    @MockK
    lateinit var companyDomainService: CompanyDomainService

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
}
