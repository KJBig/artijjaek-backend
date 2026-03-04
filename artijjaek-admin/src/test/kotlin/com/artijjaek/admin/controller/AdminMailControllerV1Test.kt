package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import com.artijjaek.admin.config.security.WebConfig
import com.artijjaek.admin.dto.response.MailDailyFailedCountResponse
import com.artijjaek.admin.dto.response.MailDailySentCountResponse
import com.artijjaek.admin.dto.response.MailOutboxPageResponse
import com.artijjaek.admin.dto.response.MailOutboxSimpleResponse
import com.artijjaek.admin.dto.request.PostArticleMailRequest
import com.artijjaek.admin.dto.request.PostNoticeMailRequest
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.admin.service.AdminMailService
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalDateTime

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

    @Test
    @WithMockUser(username = "1")
    @DisplayName("공지사항 이메일 요청값이 비어있으면 400 응답을 반환한다")
    fun postNoticeMailBadRequestTest() {
        // when
        val mvcResult = mockMvc.perform(
            post("/admin/v1/mail/notice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "memberIds": [],
                      "title": " ",
                      "content": ""
                    }
                    """.trimIndent()
                )
        )

        // then
        mvcResult.andExpect(status().isBadRequest)
        verify(exactly = 0) { adminMailService.sendNoticeMail(any()) }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("메일 아웃박스 목록을 조회한다")
    fun getMailOutboxListTest() {
        // given
        val response = MailOutboxPageResponse(
            pageNumber = 0,
            totalCount = 1,
            hasNext = false,
            content = listOf(
                MailOutboxSimpleResponse(
                    id = 1L,
                    mailType = EmailOutboxType.NOTICE,
                    recipientEmail = "a@test.com",
                    subject = "[아티짹] 공지",
                    status = EmailOutboxStatus.FAIL,
                    requestedBy = EmailOutboxRequestedBy.ADMIN_API,
                    attemptCount = 2,
                    maxAttempts = 5,
                    lastError = "TRANSIENT|RuntimeException: timeout",
                    requestedAt = LocalDateTime.parse("2026-02-27T12:00:00"),
                    nextRetryAt = LocalDateTime.parse("2026-02-27T12:05:00"),
                    sentAt = null,
                    manualRetryCount = 1,
                    lastRetriedByAdminId = 1L,
                    lastRetriedAt = LocalDateTime.parse("2026-02-27T12:01:00")
                )
            )
        )
        every {
            adminMailService.searchOutboxes(any(), any(), any(), any(), any(), any(), any())
        } returns response

        // when
        val mvcResult = mockMvc.perform(
            get("/admin/v1/mail/outbox/list")
                .param("page", "0")
                .param("size", "20")
                .param("status", "FAIL")
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.totalCount").value(1))
            .andExpect(jsonPath("$.data.content[0].id").value(1))
            .andExpect(jsonPath("$.data.content[0].status").value("FAIL"))
        verify(exactly = 1) { adminMailService.searchOutboxes(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("FAIL 혹은 DEAD 메일 아웃박스를 수동 재시도한다")
    fun patchMailOutboxRetryTest() {
        // given
        every { adminMailService.retryOutbox(10L, true, 1L) } returns Unit

        // when
        val mvcResult = mockMvc.perform(
            patch("/admin/v1/mail/outbox/10/retry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "resetAttempts": true
                    }
                    """.trimIndent()
                )
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
        verify(exactly = 1) { adminMailService.retryOutbox(10L, true, 1L) }
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("일자별 이메일 전송 성공 수를 조회한다")
    fun getDailySentCountsTest() {
        // given
        every {
            adminMailService.getDailySentCounts(
                startDate = LocalDate.of(2026, 2, 1),
                endDate = LocalDate.of(2026, 2, 2),
                requestedBy = EmailOutboxRequestedBy.ADMIN_API
            )
        } returns listOf(
            MailDailySentCountResponse(date = LocalDate.of(2026, 2, 1), sentCount = 5),
            MailDailySentCountResponse(date = LocalDate.of(2026, 2, 2), sentCount = 0)
        )

        // when
        val mvcResult = mockMvc.perform(
            get("/admin/v1/mail/outbox/sent/daily")
                .param("startDate", "2026-02-01")
                .param("endDate", "2026-02-02")
                .param("requestedBy", "ADMIN_API")
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data[0].date").value("2026-02-01"))
            .andExpect(jsonPath("$.data[0].sentCount").value(5))
            .andExpect(jsonPath("$.data[1].sentCount").value(0))
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("일자별 이메일 전송 실패 수를 조회한다")
    fun getDailyFailedCountsTest() {
        // given
        every {
            adminMailService.getDailyFailedCounts(
                startDate = LocalDate.of(2026, 2, 1),
                endDate = LocalDate.of(2026, 2, 2),
                requestedBy = null
            )
        } returns listOf(
            MailDailyFailedCountResponse(date = LocalDate.of(2026, 2, 1), failedCount = 1),
            MailDailyFailedCountResponse(date = LocalDate.of(2026, 2, 2), failedCount = 3)
        )

        // when
        val mvcResult = mockMvc.perform(
            get("/admin/v1/mail/outbox/failed/daily")
                .param("startDate", "2026-02-01")
                .param("endDate", "2026-02-02")
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data[0].failedCount").value(1))
            .andExpect(jsonPath("$.data[1].failedCount").value(3))
    }
}
