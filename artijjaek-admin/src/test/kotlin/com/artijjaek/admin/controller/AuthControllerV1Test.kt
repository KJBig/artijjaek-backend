package com.artijjaek.admin.controller

import com.artijjaek.admin.dto.request.LoginRequest
import com.artijjaek.admin.dto.response.LoginResponse
import com.artijjaek.admin.service.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@WebMvcTest(AuthControllerV1::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var authService: AuthService

    @Test
    @DisplayName("관리자 로그인에 성공한다")
    fun loginAdminTest() {
        // given
        val request = LoginRequest(
            email = "admin@test.com",
            password = "password123"
        )
        val response = LoginResponse(
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )
        every { authService.login(request) } returns response

        // when
        val mvcResult = mockMvc.perform(
            post("/admin/v1/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.accessToken").value("access-token"))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
        verify(exactly = 1) { authService.login(request) }
    }
}
