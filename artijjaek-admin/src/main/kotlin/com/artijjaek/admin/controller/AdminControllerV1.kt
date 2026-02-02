package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.CurrentAdminId
import com.artijjaek.admin.dto.common.SuccessResponse
import com.artijjaek.admin.dto.request.PatchPasswordRequest
import com.artijjaek.admin.service.AdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/v1/admin")
class AdminControllerV1(
    private val adminService: AdminService,
) {
    @PatchMapping("/password")
    fun patchAdminPassword(
        @CurrentAdminId adminId: Long,
        @RequestBody request: PatchPasswordRequest
    ): ResponseEntity<SuccessResponse> {
        adminService.changeAdminPassword(adminId, request)
        return ResponseEntity.ok().body(SuccessResponse())
    }
}