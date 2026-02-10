package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import com.artijjaek.admin.config.security.WebConfig
import com.artijjaek.admin.dto.request.PutArticleRequest
import com.artijjaek.admin.dto.response.ArticleCategoryResponse
import com.artijjaek.admin.dto.response.ArticleCompanyResponse
import com.artijjaek.admin.dto.response.ArticleDetailResponse
import com.artijjaek.admin.dto.response.ArticleListPageResponse
import com.artijjaek.admin.dto.response.ArticleSimpleResponse
import com.artijjaek.admin.enums.ArticleListSortBy
import com.artijjaek.admin.service.AdminArticleService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.http.MediaType
import java.time.LocalDateTime

@ActiveProfiles("test")
@WebMvcTest(AdminArticleControllerV1::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebConfig::class, AuthAdminIdArgumentResolver::class)
class AdminArticleControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminArticleService: AdminArticleService

    @Test
    @WithMockUser(username = "1")
    @DisplayName("아티클 상세 정보를 조회한다")
    fun getArticleDetailTest() {
        // given
        val response = ArticleDetailResponse(
            articleId = 100L,
            title = "백엔드 개발자 채용",
            description = "설명",
            image = "https://image.example.com/100.png",
            link = "https://example.com/article/100",
            company = ArticleCompanyResponse(
                companyId = 10L,
                companyNameKr = "회사A",
                logo = "https://logo.example.com/company-a.png"
            ),
            category = ArticleCategoryResponse(
                categoryId = 20L,
                categoryName = "백엔드"
            ),
            createdAt = LocalDateTime.of(2025, 1, 10, 12, 0)
        )
        every { adminArticleService.getArticleDetail(100L) } returns response

        // when & then
        mockMvc.perform(get("/admin/v1/article/100"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.articleId").value(100))
            .andExpect(jsonPath("$.data.company.companyNameKr").value("회사A"))
            .andExpect(jsonPath("$.data.category.categoryName").value("백엔드"))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("아티클 정보를 수정한다")
    fun putArticleTest() {
        // given
        val request = PutArticleRequest(
            title = "수정된 제목",
            description = "수정된 설명",
            image = "https://image.example.com/updated.png",
            link = "https://example.com/article/updated",
            companyId = 10L,
            categoryId = 20L
        )
        every { adminArticleService.updateArticle(100L, request) } returns Unit

        // when & then
        mockMvc.perform(
            put("/admin/v1/article/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "수정된 제목",
                      "description": "수정된 설명",
                      "image": "https://image.example.com/updated.png",
                      "link": "https://example.com/article/updated",
                      "companyId": 10,
                      "categoryId": 20
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.message").value("요청성공"))

        verify(exactly = 1) { adminArticleService.updateArticle(100L, request) }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("아티클 목록을 조회한다")
    fun getArticleListTest() {
        // given
        val pageable = PageRequest.of(0, 5)
        val response = ArticleListPageResponse(
            pageNumber = 0,
            totalCount = 12,
            hasNext = true,
            content = listOf(
                ArticleSimpleResponse(
                    articleId = 100L,
                    title = "백엔드 개발자 채용",
                    company = ArticleCompanyResponse(
                        companyId = 10L,
                        companyNameKr = "회사A",
                        logo = "https://logo.example.com/company-a.png"
                    ),
                    categoryName = "백엔드",
                    link = "https://example.com/article/100",
                    image = "https://image.example.com/100.png",
                    description = "설명",
                    createdAt = LocalDateTime.of(2025, 1, 10, 12, 0)
                )
            )
        )
        every {
            adminArticleService.searchArticles(
                pageable = pageable,
                companyId = 10L,
                categoryId = 20L,
                title = "개발자",
                sortBy = ArticleListSortBy.REGISTER_DATE,
                sortDirection = Sort.Direction.DESC
            )
        } returns response

        // when & then
        mockMvc.perform(
            get("/admin/v1/article/list")
                .param("page", "0")
                .param("size", "5")
                .param("companyId", "10")
                .param("categoryId", "20")
                .param("title", "개발자")
                .param("sortBy", "REGISTER_DATE")
                .param("sortDirection", "DESC")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.totalCount").value(12))
            .andExpect(jsonPath("$.data.content[0].title").value("백엔드 개발자 채용"))
            .andExpect(jsonPath("$.data.content[0].company.companyNameKr").value("회사A"))
            .andExpect(jsonPath("$.data.content[0].company.logo").value("https://logo.example.com/company-a.png"))
            .andExpect(jsonPath("$.data.content[0].image").value("https://image.example.com/100.png"))
            .andExpect(jsonPath("$.data.content[0].description").value("설명"))

        verify(exactly = 1) {
            adminArticleService.searchArticles(
                pageable = pageable,
                companyId = 10L,
                categoryId = 20L,
                title = "개발자",
                sortBy = ArticleListSortBy.REGISTER_DATE,
                sortDirection = Sort.Direction.DESC
            )
        }
    }
}
