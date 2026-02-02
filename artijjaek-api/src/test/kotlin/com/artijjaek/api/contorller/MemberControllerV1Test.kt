package com.artijjaek.api.contorller

import com.artijjaek.api.config.ApiSecurityConfig
import com.artijjaek.api.controller.MemberControllerV1
import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.service.MemberService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

}