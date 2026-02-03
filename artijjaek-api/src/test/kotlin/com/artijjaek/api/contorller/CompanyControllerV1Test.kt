package com.artijjaek.api.contorller

import com.artijjaek.api.config.ApiSecurityConfig
import com.artijjaek.api.controller.CompanyControllerV1
import com.artijjaek.api.dto.common.PageResponse
import com.artijjaek.api.dto.response.CompanySimpleDataResponse
import com.artijjaek.api.service.CompanyService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@WebMvcTest(CompanyControllerV1::class)
@Import(ApiSecurityConfig::class)
class CompanyControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var companyService: CompanyService

    @Test
    @DisplayName("회사 목록 조회")
    fun getCompaniesTest() {
        // given
        val data = CompanySimpleDataResponse(
            companyId = 1L,
            companyNameKr = "회사",
            companyNameEn = "Company",
            companyImageUrl = "http://example.com/image.png"
        )

        val content = listOf(data)
        val companyPage = PageImpl(
            listOf(data),
            PageRequest.of(0, 1),
            1
        )

        val response = PageResponse(companyPage.pageable.pageNumber, companyPage.hasNext(), content)

        every { companyService.searchCompanyList(any()) }.returns(response)

        // when
        val mvcResult = mockMvc.perform(
            get("/api/v1/company/list")
                .param("page", "0")
                .param("size", "1")
                .contentType(APPLICATION_JSON)
        )

        // then
        mvcResult
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.pageNumber").value(0))
            .andExpect(jsonPath("$.data.hasNext").value(false))
            .andExpect(jsonPath("$.data.content[0].companyId").value(1))
    }

}