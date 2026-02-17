package com.artijjaek.api.contorller

import com.artijjaek.api.config.ApiSecurityConfig
import com.artijjaek.api.controller.InquiryControllerV1
import com.artijjaek.api.dto.request.InquiryRequest
import com.artijjaek.api.service.InquiryService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ActiveProfiles("test")
@WebMvcTest(InquiryControllerV1::class)
@Import(ApiSecurityConfig::class)
class InquiryControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var inquiryService: InquiryService

    @Test
    @DisplayName("문의하기")
    fun postInquiryTest() {
        // given
        val request = InquiryRequest(
            email = "test@example.com",
            content = "some inquiry content"
        )

        justRun { inquiryService.saveInquiry(any()) }

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/inquiry")
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
    @DisplayName("문의하기 이메일 형식 검증 실패시 400")
    fun postInquiryInvalidEmailValidationFailTest() {
        // given
        val request = InquiryRequest(
            email = "invalid-email",
            content = "some inquiry content"
        )

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/inquiry")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { inquiryService.saveInquiry(any<InquiryRequest>()) }
    }

    @Test
    @DisplayName("문의하기 이메일 길이 검증 실패시 400")
    fun postInquiryEmailLengthValidationFailTest() {
        // given
        val request = InquiryRequest(
            email = buildOverLengthEmail(),
            content = "some inquiry content"
        )

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/inquiry")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { inquiryService.saveInquiry(any<InquiryRequest>()) }
    }

    @Test
    @DisplayName("문의하기 내용 비어있으면 400")
    fun postInquiryEmptyContentValidationFailTest() {
        // given
        val request = InquiryRequest(
            email = "test@example.com",
            content = ""
        )

        // when
        val mvcResult = mockMvc.perform(
            post("/api/v1/inquiry")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        mvcResult
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
        verify(exactly = 0) { inquiryService.saveInquiry(any<InquiryRequest>()) }
    }

    private fun buildOverLengthEmail(): String {
        return "${"a".repeat(250)}@a.com"
    }

}
