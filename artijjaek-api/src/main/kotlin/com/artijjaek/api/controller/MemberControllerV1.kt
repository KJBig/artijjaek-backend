package com.artijjaek.api.controller

import com.artijjaek.api.dto.common.SuccessDataResponse
import com.artijjaek.api.dto.common.SuccessResponse
import com.artijjaek.api.dto.request.CheckMemberTokenAvailabilityRequest
import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.dto.response.MemberTokenAvailabilityResponse
import com.artijjaek.api.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/member")
@RestController
class MemberControllerV1(
    private val memberService: MemberService,
) {

    @PostMapping("/register")
    fun registerMember(@RequestBody request: RegisterMemberRequest): ResponseEntity<SuccessResponse> {
        memberService.register(request)
        return ResponseEntity.ok(SuccessResponse())
    }

    @PostMapping("/token-availability")
    fun checkMemberTokenAvailability(@RequestBody request: CheckMemberTokenAvailabilityRequest): ResponseEntity<SuccessDataResponse<MemberTokenAvailabilityResponse>?> {
        val response = memberService.checkTokenAvailability(request)
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

}