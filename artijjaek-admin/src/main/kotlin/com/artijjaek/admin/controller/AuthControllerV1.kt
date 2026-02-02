package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.CurrentAdminId
import com.artijjaek.admin.dto.common.SuccessDataResponse
import com.artijjaek.admin.dto.common.SuccessResponse
import com.artijjaek.admin.dto.request.LoginRequest
import com.artijjaek.admin.dto.request.RefreshRequest
import com.artijjaek.admin.dto.response.LoginResponse
import com.artijjaek.admin.dto.response.RefreshResponse
import com.artijjaek.admin.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/v1/auth")
class AuthControllerV1(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun loginAdmin(@RequestBody request: LoginRequest): ResponseEntity<SuccessDataResponse<LoginResponse>> {
        val response = authService.login(request)
        return ResponseEntity.ok().body(SuccessDataResponse(response))
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody request: RefreshRequest): ResponseEntity<SuccessDataResponse<RefreshResponse>> {
        val response = authService.refreshAccessToken(request)
        return ResponseEntity.ok().body(SuccessDataResponse(response))
    }

    @PostMapping("/logout")
    fun logoutAdmin(@CurrentAdminId adminId: Long): ResponseEntity<SuccessResponse> {
        authService.logout(adminId)
        return ResponseEntity.ok().body(SuccessResponse())
    }
}