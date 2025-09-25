package com.artiting.api.controller

import com.artiting.api.dto.common.SuccessDataResponse
import com.artiting.api.dto.common.SuccessResponse
import com.artiting.api.dto.request.RegisterMemberRequest
import com.artiting.api.dto.response.MemberTokenAvailabilityResponse
import com.artiting.api.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/token-availability")
    fun checkMemberTokenAvailability(
        @RequestParam("email") email: String,
        @RequestParam("token") token: String
    ): ResponseEntity<SuccessDataResponse<MemberTokenAvailabilityResponse>?> {
        val response = memberService.checkTokenAvailability(email, token)
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

}