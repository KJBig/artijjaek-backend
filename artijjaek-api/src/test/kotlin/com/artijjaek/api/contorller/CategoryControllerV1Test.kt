package com.artijjaek.api.contorller

import com.artijjaek.api.config.ApiSecurityConfig
import com.artijjaek.api.controller.CategoryControllerV1
import com.artijjaek.api.dto.common.PageResponse
import com.artijjaek.api.dto.response.CategorySimpleDataResponse
import com.artijjaek.api.service.CategoryService
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
@WebMvcTest(CategoryControllerV1::class)
@Import(ApiSecurityConfig::class)
class CategoryControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var categoryService: CategoryService

    @Test
    @DisplayName("카테고리 목록 조회")
    fun getCategoriesTest() {
        // given
        val data = CategorySimpleDataResponse(
            categoryId = 1L,
            categoryName = "카테고리"
        )

        val content = listOf(data)
        val categoryPage = PageImpl(
            listOf(data),
            PageRequest.of(0, 1),
            1
        )

        val response = PageResponse(categoryPage.pageable.pageNumber, categoryPage.hasNext(), content)

        every { categoryService.searchCategoryList(any()) }.returns(response)

        // when
        val mvcResult = mockMvc.perform(
            get("/api/v1/category/list")
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
            .andExpect(jsonPath("$.data.content[0].categoryId").value(1))
    }

}