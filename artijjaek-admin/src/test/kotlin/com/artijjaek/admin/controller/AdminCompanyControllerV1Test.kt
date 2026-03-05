package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import com.artijjaek.admin.config.security.WebConfig
import com.artijjaek.admin.dto.request.PostCompanyRequest
import com.artijjaek.admin.dto.request.PutCompanyRequest
import com.artijjaek.admin.dto.response.CompanyListPageResponse
import com.artijjaek.admin.dto.response.CompanySimpleResponse
import com.artijjaek.admin.dto.response.MemberOptionCompanyResponse
import com.artijjaek.admin.dto.response.PostCompanyResponse
import com.artijjaek.admin.dto.response.TopSubscribedCompanyResponse
import com.artijjaek.admin.service.AdminCompanyService
import com.artijjaek.core.domain.company.enums.CrawlOrder
import com.artijjaek.core.domain.company.enums.CrawlPattern
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.http.MediaType

@ActiveProfiles("test")
@WebMvcTest(AdminCompanyControllerV1::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebConfig::class, AuthAdminIdArgumentResolver::class)
class AdminCompanyControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminCompanyService: AdminCompanyService

    @Autowired
    lateinit var objectMapper: com.fasterxml.jackson.databind.ObjectMapper

    @Test
    @WithMockUser(username = "1")
    @DisplayName("회원 편집 드롭다운 회사 옵션을 조회한다")
    fun getMemberCompanyOptionsTest() {
        // given
        val response = listOf(
            MemberOptionCompanyResponse(
                companyId = 10L,
                companyNameKr = "회사A",
                companyNameEn = "CompanyA",
                logo = "https://cdn.example.com/company-a.png"
            )
        )
        every { adminCompanyService.getMemberCompanyOptions() } returns response

        // when & then
        mockMvc.perform(get("/admin/v1/company/list"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data[0].logo").value("https://cdn.example.com/company-a.png"))

        verify(exactly = 1) { adminCompanyService.getMemberCompanyOptions() }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("많이 구독한 회사 Top 목록을 조회한다")
    fun getTopSubscribedCompaniesTest() {
        // given
        val response = listOf(
            TopSubscribedCompanyResponse(
                rank = 1,
                companyId = 10L,
                companyNameKr = "회사A",
                subscriberCount = 25
            ),
            TopSubscribedCompanyResponse(
                rank = 2,
                companyId = 11L,
                companyNameKr = "회사B",
                subscriberCount = 20
            )
        )
        every { adminCompanyService.getTopSubscribedCompanies() } returns response

        // when & then
        mockMvc.perform(get("/admin/v1/company/subscribed/top"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data[0].rank").value(1))
            .andExpect(jsonPath("$.data[0].subscriberCount").value(25))

        verify(exactly = 1) { adminCompanyService.getTopSubscribedCompanies() }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("회사 목록을 검색 조건으로 조회한다")
    fun getCompaniesTest() {
        // given
        val response = CompanyListPageResponse(
            pageNumber = 0,
            totalCount = 1,
            hasNext = false,
            content = listOf(
                CompanySimpleResponse(
                    companyId = 1L,
                    nameKr = "회사A",
                    nameEn = "CompanyA",
                    logo = "logo",
                    baseUrl = "base",
                    blogUrl = "blog",
                    crawlUrl = "crawl",
                    crawlAvailability = true,
                    crawlPattern = CrawlPattern.RSS,
                    crawlOrder = CrawlOrder.NORMAL,
                    createdAt = java.time.LocalDateTime.now()
                )
            )
        )
        every { adminCompanyService.searchCompanies(any(), "회사") } returns response

        // when & then
        mockMvc.perform(get("/admin/v1/company/manage/list").param("keyword", "회사"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.totalCount").value(1))
            .andExpect(jsonPath("$.data.content[0].nameEn").value("CompanyA"))

        verify(exactly = 1) { adminCompanyService.searchCompanies(any(), "회사") }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("회사를 등록한다")
    fun postCompanyTest() {
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
        every { adminCompanyService.createCompany(any()) } returns PostCompanyResponse(companyId = 100L)

        // when & then
        mockMvc.perform(
            post("/admin/v1/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.companyId").value(100))

        verify(exactly = 1) { adminCompanyService.createCompany(any()) }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("회사 정보를 수정한다")
    fun putCompanyTest() {
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
        every { adminCompanyService.updateCompany(1L, any()) } just runs

        // when & then
        mockMvc.perform(
            put("/admin/v1/company/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))

        verify(exactly = 1) { adminCompanyService.updateCompany(1L, any()) }
    }
}
