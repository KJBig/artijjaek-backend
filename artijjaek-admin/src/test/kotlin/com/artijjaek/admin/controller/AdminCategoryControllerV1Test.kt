package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import com.artijjaek.admin.config.security.WebConfig
import com.artijjaek.admin.dto.response.MemberOptionCategoryResponse
import com.artijjaek.admin.service.AdminCategoryService
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
@WebMvcTest(AdminCategoryControllerV1::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebConfig::class, AuthAdminIdArgumentResolver::class)
class AdminCategoryControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminCategoryService: AdminCategoryService

    @Test
    @WithMockUser(username = "1")
    @DisplayName("회원 편집 드롭다운 카테고리 옵션을 조회한다")
    fun getMemberCategoryOptionsTest() {
        // given
        val response = listOf(
            MemberOptionCategoryResponse(
                categoryId = 20L,
                categoryName = "백엔드"
            )
        )
        every { adminCategoryService.getMemberCategoryOptions() } returns response

        // when & then
        mockMvc.perform(get("/admin/v1/category/list"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data[0].categoryName").value("백엔드"))

        verify(exactly = 1) { adminCategoryService.getMemberCategoryOptions() }
    }
}
