package com.artijjaek.api.controller

import com.artijjaek.api.dto.common.SuccessResponse
import com.artijjaek.api.dto.request.InquiryRequest
import com.artijjaek.api.service.InquiryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/inquiry")
@RestController
class InquiryControllerV1(
    private val inquiryService: InquiryService,
) {

    @PostMapping("")
    fun postInquiry(@RequestBody request: InquiryRequest): ResponseEntity<SuccessResponse> {
        inquiryService.saveInquiry(request)
        return ResponseEntity.ok(SuccessResponse())
    }

}