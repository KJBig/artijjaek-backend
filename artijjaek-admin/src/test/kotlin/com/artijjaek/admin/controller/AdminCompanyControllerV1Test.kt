package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import com.artijjaek.admin.config.security.WebConfig
import com.artijjaek.admin.dto.response.MemberOptionCompanyResponse
import com.artijjaek.admin.dto.response.TopSubscribedCompanyResponse
import com.artijjaek.admin.service.AdminCompanyService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@WebMvcTest(AdminCompanyControllerV1::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebConfig::class, AuthAdminIdArgumentResolver::class)
class AdminCompanyControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminCompanyService: AdminCompanyService

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
}
