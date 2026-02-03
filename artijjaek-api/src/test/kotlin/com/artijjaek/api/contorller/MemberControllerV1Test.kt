package com.artijjaek.api.contorller

import com.artijjaek.api.config.ApiSecurityConfig
import com.artijjaek.api.controller.MemberControllerV1
import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.dto.request.SubscriptionChangeRequest
import com.artijjaek.api.dto.request.UnsubscriptionRequest
import com.artijjaek.api.dto.response.MemberDataResponse
import com.artijjaek.api.service.MemberService
import com.artijjaek.core.domain.unsubscription.enums.UnSubscriptionReason
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
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
    @DisplayName("사용자 토큰을 통해 구독정보 조회")
    fun getMemberDataWithUuIdTokenTest() {
        // given
        val email = "test@example.com"
        val uuIdToken = "some-uuid-token"

        val response = MemberDataResponse(
            email = email,
            nickname = "password123",
            companyIds = listOf(1L, 2L),
            categoryIds = listOf(1L, 2L)
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

}