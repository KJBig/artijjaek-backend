package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import com.artijjaek.admin.config.security.WebConfig
import com.artijjaek.admin.dto.request.PostArticleMailRequest
import com.artijjaek.admin.dto.request.PostNoticeMailRequest
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.admin.service.AdminMailService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@WebMvcTest(AdminMailControllerV1::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebConfig::class, AuthAdminIdArgumentResolver::class)
class AdminMailControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminMailService: AdminMailService

    @Test
    @WithMockUser(username = "1")
    @DisplayName("특정 회원들에게 환영 이메일을 수동 발송한다")
    fun postWelcomeMailTest() {
        // given
        val request = PostWelcomeMailRequest(memberIds = listOf(1L, 2L, 3L))
        every { adminMailService.sendWelcomeMail(request) } returns Unit

        // when
        val mvcResult = mockMvc.perform(
            post("/admin/v1/mail/welcome")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "memberIds": [1, 2, 3]
                    }
                    """.trimIndent()
                )
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.message").value("요청성공"))
        verify(exactly = 1) { adminMailService.sendWelcomeMail(request) }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("특정 회원들에게 특정 아티클 목록 이메일을 수동 발송한다")
    fun postArticleMailTest() {
        // given
        val request = PostArticleMailRequest(
            memberIds = listOf(1L, 2L, 3L),
            articleIds = listOf(10L, 11L)
        )
        every { adminMailService.sendArticleMail(request) } returns Unit

        // when
        val mvcResult = mockMvc.perform(
            post("/admin/v1/mail/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "memberIds": [1, 2, 3],
                      "articleIds": [10, 11]
                    }
                    """.trimIndent()
                )
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.message").value("요청성공"))
        verify(exactly = 1) { adminMailService.sendArticleMail(request) }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("특정 회원들에게 공지사항 이메일을 수동 발송한다")
    fun postNoticeMailTest() {
        // given
        val request = PostNoticeMailRequest(
            memberIds = listOf(1L, 2L, 3L),
            title = "구독 가능한 회사가 추가되었습니다.",
            content = "카카오엔터프라이즈 블로그가 추가되었습니다.\n구독 설정에서 선택해주세요."
        )
        every { adminMailService.sendNoticeMail(request) } returns Unit

        // when
        val mvcResult = mockMvc.perform(
            post("/admin/v1/mail/notice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "memberIds": [1, 2, 3],
                      "title": "구독 가능한 회사가 추가되었습니다.",
                      "content": "카카오엔터프라이즈 블로그가 추가되었습니다.\n구독 설정에서 선택해주세요."
                    }
                    """.trimIndent()
                )
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.message").value("요청성공"))
        verify(exactly = 1) { adminMailService.sendNoticeMail(request) }
    }
}
