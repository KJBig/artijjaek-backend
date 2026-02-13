package com.artijjaek.api.dto.response

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import com.artijjaek.core.domain.company.entity.Company
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class CompanySimpleDataResponseTest {

    @Test
    @DisplayName("Company 엔티티로부터 CompanySimpleDataResponse를 생성할 수 있다")
    fun fromTest() {
        // given
        val company = Company(
            id = 1L,
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )

        // when
        val response = CompanySimpleDataResponse.from(company)

        // then
        assertThat(response.companyId).isEqualTo(1L)
        assertThat(response.companyNameKr).isEqualTo("회사")
        assertThat(response.companyNameEn).isEqualTo("Company")
        assertThat(response.companyImageUrl).isEqualTo("http://example.com/logo1.png")
        assertThat(response.companyBlogUrl).isEqualTo("http://example.com/blog")
    }

    @Test
    @DisplayName("Id가 없는 Company 엔티티로부터 CompanySimpleDataResponse를 생성할 수 없다")
    fun fromTest_NoCompanyId() {
        // given
        val company = Company(
            nameKr = "회사",
            nameEn = "Company",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example.com",
            blogUrl = "http://example.com/blog",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )

        // when
        val exception = assertThrows(ApplicationException::class.java) {
            CompanySimpleDataResponse.from(company)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.COMPANY_ID_MISSING_ERROR.code)
    }
}
