package com.artijjaek.admin.controller

import com.artijjaek.admin.dto.common.SuccessResponse
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.admin.service.AdminMailService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/v1/mail")
class AdminMailControllerV1(
    private val adminMailService: AdminMailService,
) {

    @PostMapping("/welcome")
    fun postWelcomeMail(
        @RequestBody request: PostWelcomeMailRequest,
    ): ResponseEntity<SuccessResponse> {
        adminMailService.sendWelcomeMail(request)
        return ResponseEntity.ok(SuccessResponse())
    }
}
