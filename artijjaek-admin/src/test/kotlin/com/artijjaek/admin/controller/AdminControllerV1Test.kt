package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import com.artijjaek.admin.config.security.WebConfig
import com.artijjaek.admin.dto.request.PatchPasswordRequest
import com.artijjaek.admin.service.AdminService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@WebMvcTest(AdminControllerV1::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebConfig::class, AuthAdminIdArgumentResolver::class)
class AdminControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var adminService: AdminService

    @Test
    @WithMockUser(username = "1")
    @DisplayName("관리자 비밀번호를 변경한다")
    fun patchAdminPasswordTest() {
        // given
        val request = PatchPasswordRequest(
            oldPassword = "old-password",
            newPassword = "new-password"
        )
        justRun { adminService.changeAdminPassword(1L, request) }

        // when
        val mvcResult = mockMvc.perform(
            patch("/admin/v1/admin/password")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
        verify(exactly = 1) { adminService.changeAdminPassword(1L, request) }
    }
}
