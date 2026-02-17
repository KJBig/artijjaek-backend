package com.artijjaek.api.contorller

import com.artijjaek.api.config.ApiSecurityConfig
import com.artijjaek.api.controller.MemberControllerV1
import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.dto.request.SubscriptionChangeRequest
import com.artijjaek.api.dto.request.UnsubscriptionRequest
import com.artijjaek.api.dto.response.CategorySimpleDataResponse
import com.artijjaek.api.dto.response.CompanySimpleDataResponse
import com.artijjaek.api.dto.response.MemberDataResponse
import com.artijjaek.api.service.MemberService
import com.artijjaek.core.domain.unsubscription.enums.UnSubscriptionReason
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@WebMvcTest(MemberControllerV1::class)
@Import(ApiSecurityConfig::class)
class MemberControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var memberService: MemberService

    @Test
    @DisplayName("구독하기")
    fun registerMemberTest() {
        // given
        val request = RegisterMemberRequest(
            email = "test@example.com",
            nickname = "password123",
            categoryIds = mutableListOf(1),
            companyIds = mutableListOf(1)
        )

        justRun { memberService.register(any<RegisterMemberRequest>()) }

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/member/register")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))

        )

        // then
        mvcResult
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
    }

    @Test
    @DisplayName("구독하기 이메일 형식 검증 실패시 400")
    fun registerMemberValidationFailTest() {
        // given
        val invalidRequest = RegisterMemberRequest(
            email = "invalid-email",
            nickname = "nickname",
            categoryIds = mutableListOf(1L),
            companyIds = mutableListOf(1L)
        )

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/member/register")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.register(any<RegisterMemberRequest>()) }
    }

    @Test
    @DisplayName("구독하기 이메일 길이 검증 실패시 400")
    fun registerMemberEmailLengthValidationFailTest() {
        // given
        val invalidRequest = RegisterMemberRequest(
            email = buildOverLengthEmail(),
            nickname = "nickname",
            categoryIds = mutableListOf(1L),
            companyIds = mutableListOf(1L)
        )

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/member/register")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.register(any<RegisterMemberRequest>()) }
    }

    @Test
    @DisplayName("구독하기 닉네임 길이 검증 실패시 400")
    fun registerMemberNicknameLengthValidationFailTest() {
        // given
        val invalidRequest = RegisterMemberRequest(
            email = "test@example.com",
            nickname = "n".repeat(256),
            categoryIds = mutableListOf(1L),
            companyIds = mutableListOf(1L)
        )

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/member/register")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.register(any<RegisterMemberRequest>()) }
    }

    @Test
    @DisplayName("구독하기 categoryIds 빈 목록 요청시 400")
    fun registerMemberCategoryIdsValidationFailTest() {
        // given
        val invalidRequest = RegisterMemberRequest(
            email = "test@example.com",
            nickname = "nickname",
            categoryIds = emptyList(),
            companyIds = mutableListOf(1L)
        )

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/member/register")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.register(any<RegisterMemberRequest>()) }
    }

    @Test
    @DisplayName("구독하기 companyIds 빈 목록 요청시 400")
    fun registerMemberCompanyIdsValidationFailTest() {
        // given
        val invalidRequest = RegisterMemberRequest(
            email = "test@example.com",
            nickname = "nickname",
            categoryIds = mutableListOf(1L),
            companyIds = emptyList()
        )

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/member/register")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.register(any<RegisterMemberRequest>()) }
    }

    @Test
    @DisplayName("구독하기 닉네임 공백 요청시 400")
    fun registerMemberBlankNicknameValidationFailTest() {
        // given
        val invalidRequest = """
            {
              "email": "test@example.com",
              "nickname": "   ",
              "categoryIds": [1],
              "companyIds": [1]
            }
        """.trimIndent()

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/member/register")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(invalidRequest)
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.register(any()) }
    }

    @Test
    @DisplayName("사용자 토큰을 통해 구독정보 조회")
    fun getMemberDataWithUuIdTokenTest() {
        // given
        val email = "test@example.com"
        val uuIdToken = "some-uuid-token"

        val response = MemberDataResponse(
            email = email,
            nickname = "password123",
            companies = listOf(
                CompanySimpleDataResponse(1L, "회사1", "Company1", "https://example.com/logo1.png", "https://example.com/blog1"),
                CompanySimpleDataResponse(2L, "회사2", "Company2", "https://example.com/logo2.png", "https://example.com/blog2")
            ),
            categories = listOf(
                CategorySimpleDataResponse(1L, "카테고리1"),
                CategorySimpleDataResponse(2L, "카테고리2")
            )
        )

        every { memberService.getMemberDataWithToken(email, uuIdToken) }.returns(response)

        // when
        val mvcResult = mockMvc.perform(
            get("/api/v1/member/data")
                .param("email", email)
                .param("token", uuIdToken)
                .contentType(APPLICATION_JSON)
        )

        // then
        mvcResult
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.email").value(email))
    }

    @Test
    @DisplayName("사용자 구독 정보 변경")
    fun changeSubscriptionTest() {
        // given
        val email = "test@example.com"
        val uuIdToken = "some-uuid-token"

        val request = SubscriptionChangeRequest(
            email = email,
            token = uuIdToken,
            nickname = "nickname",
            categoryIds = mutableListOf(1L),
            companyIds = mutableListOf(1L),
        )

        justRun { memberService.changeSubscription(any<SubscriptionChangeRequest>()) }

        // when
        val mvcResult = mockMvc.perform(
            put("/api/v1/member/subscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        mvcResult
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
    }

    @Test
    @DisplayName("구독 변경 token null 요청시 400")
    fun changeSubscriptionTokenNullValidationFailTest() {
        // given
        val invalidRequest = """
            {
              "email": "test@example.com",
              "token": null,
              "nickname": "nickname",
              "categoryIds": [1],
              "companyIds": [1]
            }
        """.trimIndent()

        // when
        val mvcResult = mockMvc.perform(
            put("/api/v1/member/subscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(invalidRequest)
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.changeSubscription(any()) }
    }

    @Test
    @DisplayName("구독 변경 이메일 형식 검증 실패시 400")
    fun changeSubscriptionInvalidEmailValidationFailTest() {
        // given
        val invalidRequest = SubscriptionChangeRequest(
            email = "invalid-email",
            token = "some-uuid-token",
            nickname = "nickname",
            categoryIds = mutableListOf(1L),
            companyIds = mutableListOf(1L)
        )

        // when
        val mvcResult = mockMvc.perform(
            put("/api/v1/member/subscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.changeSubscription(any<SubscriptionChangeRequest>()) }
    }

    @Test
    @DisplayName("구독 변경 이메일 길이 검증 실패시 400")
    fun changeSubscriptionEmailLengthValidationFailTest() {
        // given
        val invalidRequest = SubscriptionChangeRequest(
            email = buildOverLengthEmail(),
            token = "some-uuid-token",
            nickname = "nickname",
            categoryIds = mutableListOf(1L),
            companyIds = mutableListOf(1L)
        )

        // when
        val mvcResult = mockMvc.perform(
            put("/api/v1/member/subscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.changeSubscription(any<SubscriptionChangeRequest>()) }
    }

    @Test
    @DisplayName("구독 변경 닉네임 길이 검증 실패시 400")
    fun changeSubscriptionNicknameLengthValidationFailTest() {
        // given
        val invalidRequest = SubscriptionChangeRequest(
            email = "test@example.com",
            token = "some-uuid-token",
            nickname = "n".repeat(256),
            categoryIds = mutableListOf(1L),
            companyIds = mutableListOf(1L)
        )

        // when
        val mvcResult = mockMvc.perform(
            put("/api/v1/member/subscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.changeSubscription(any<SubscriptionChangeRequest>()) }
    }

    @Test
    @DisplayName("구독 변경 닉네임 공백 요청시 400")
    fun changeSubscriptionBlankNicknameValidationFailTest() {
        // given
        val invalidRequest = """
            {
              "email": "test@example.com",
              "token": "some-uuid-token",
              "nickname": "   ",
              "categoryIds": [1],
              "companyIds": [1]
            }
        """.trimIndent()

        // when
        val mvcResult = mockMvc.perform(
            put("/api/v1/member/subscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(invalidRequest)
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.changeSubscription(any<SubscriptionChangeRequest>()) }
    }

    @Test
    @DisplayName("구독 변경 categoryIds 빈 목록 요청시 400")
    fun changeSubscriptionCategoryIdsValidationFailTest() {
        // given
        val invalidRequest = SubscriptionChangeRequest(
            email = "test@example.com",
            token = "some-uuid-token",
            nickname = "nickname",
            categoryIds = emptyList(),
            companyIds = mutableListOf(1L)
        )

        // when
        val mvcResult = mockMvc.perform(
            put("/api/v1/member/subscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.changeSubscription(any<SubscriptionChangeRequest>()) }
    }

    @Test
    @DisplayName("구독 변경 companyIds 빈 목록 요청시 400")
    fun changeSubscriptionCompanyIdsValidationFailTest() {
        // given
        val invalidRequest = SubscriptionChangeRequest(
            email = "test@example.com",
            token = "some-uuid-token",
            nickname = "nickname",
            categoryIds = mutableListOf(1L),
            companyIds = emptyList()
        )

        // when
        val mvcResult = mockMvc.perform(
            put("/api/v1/member/subscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { memberService.changeSubscription(any<SubscriptionChangeRequest>()) }
    }

    @Test
    @DisplayName("구독 해지")
    fun cancelSubscriptionTest() {
        // given
        val email = "test@example.com"
        val uuIdToken = "some-uuid-token"

        val request = UnsubscriptionRequest(
            email = email,
            token = uuIdToken,
            reason = UnSubscriptionReason.NO_COMPANY,
            detail = "reason detail"
        )

        justRun { memberService.cancelSubscription(any<UnsubscriptionRequest>()) }

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/member/unsubscription")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        mvcResult
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
    }

    private fun buildOverLengthEmail(): String {
        return "${"a".repeat(250)}@a.com"
    }

}
