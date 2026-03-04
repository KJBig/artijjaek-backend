package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.CurrentAdminId
import com.artijjaek.admin.dto.common.SuccessResponse
import com.artijjaek.admin.dto.common.SuccessDataResponse
import com.artijjaek.admin.dto.request.PatchMailOutboxRetryRequest
import com.artijjaek.admin.dto.request.PostArticleMailRequest
import com.artijjaek.admin.dto.request.PostNoticeMailRequest
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.admin.dto.response.MailOutboxPageResponse
import com.artijjaek.admin.service.AdminMailService
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

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

    @PostMapping("/article")
    fun postArticleMail(
        @RequestBody request: PostArticleMailRequest,
    ): ResponseEntity<SuccessResponse> {
        adminMailService.sendArticleMail(request)
        return ResponseEntity.ok(SuccessResponse())
    }

    @PostMapping("/notice")
    fun postNoticeMail(
        @Valid @RequestBody request: PostNoticeMailRequest,
    ): ResponseEntity<SuccessResponse> {
        adminMailService.sendNoticeMail(request)
        return ResponseEntity.ok(SuccessResponse())
    }

    @GetMapping("/outbox/list")
    fun getMailOutboxes(
        pageable: Pageable,
        @RequestParam(required = false) status: EmailOutboxStatus?,
        @RequestParam(required = false) mailType: EmailOutboxType?,
        @RequestParam(required = false) requestedBy: EmailOutboxRequestedBy?,
        @RequestParam(required = false) recipientEmail: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) requestedAtFrom: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) requestedAtTo: LocalDateTime?,
    ): ResponseEntity<SuccessDataResponse<MailOutboxPageResponse>> {
        val response = adminMailService.searchOutboxes(
            pageable = pageable,
            status = status,
            mailType = mailType,
            requestedBy = requestedBy,
            recipientEmail = recipientEmail,
            requestedAtFrom = requestedAtFrom,
            requestedAtTo = requestedAtTo
        )
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

    @PatchMapping("/outbox/{outboxId}/retry")
    fun patchMailOutboxRetry(
        @CurrentAdminId adminId: Long,
        @PathVariable outboxId: Long,
        @RequestBody(required = false) request: PatchMailOutboxRetryRequest?,
    ): ResponseEntity<SuccessResponse> {
        adminMailService.retryOutbox(outboxId, request?.resetAttempts ?: false, adminId)
        return ResponseEntity.ok(SuccessResponse())
    }
}
