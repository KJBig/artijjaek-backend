package com.artijjaek.admin.controller

import com.artijjaek.admin.dto.common.SuccessDataResponse
import com.artijjaek.admin.dto.request.LoginRequest
import com.artijjaek.admin.dto.request.RefreshRequest
import com.artijjaek.admin.dto.response.LoginResponse
import com.artijjaek.admin.dto.response.RefreshResponse
import com.artijjaek.admin.service.AdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/v1/auth")
class AuthControllerV1(
    private val adminService: AdminService,
) {

    @PostMapping("/login")
    fun loginAdmin(@RequestBody request: LoginRequest): ResponseEntity<SuccessDataResponse<LoginResponse>> {
        val response = adminService.login(request)
        return ResponseEntity.ok().body(SuccessDataResponse(response))
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody request: RefreshRequest): ResponseEntity<SuccessDataResponse<RefreshResponse>> {
        val response = adminService.refreshAccessToken(request)
        return ResponseEntity.ok().body(SuccessDataResponse(response))
    }
}