package com.artijjaek.api.controller

import com.artijjaek.api.dto.common.SuccessDataResponse
import com.artijjaek.api.dto.common.SuccessResponse
import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.dto.request.SubscriptionChangeRequest
import com.artijjaek.api.dto.request.UnsubscriptionRequest
import com.artijjaek.api.dto.response.MemberDataResponse
import com.artijjaek.api.service.MemberService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/member")
@RestController
class MemberControllerV1(
    private val memberService: MemberService,
) {

    @PostMapping("/register")
    fun registerMember(@Valid @RequestBody request: RegisterMemberRequest): ResponseEntity<SuccessResponse> {
        memberService.register(request)
        return ResponseEntity.ok(SuccessResponse())
    }

    @GetMapping("/data")
    fun getMemberDataWithToken(
        @RequestParam email: String,
        @RequestParam token: String
    ): ResponseEntity<SuccessDataResponse<MemberDataResponse>> {
        val response = memberService.getMemberDataWithToken(email, token)
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

    @PutMapping("/subscription")
    fun changeSubscription(@Valid @RequestBody request: SubscriptionChangeRequest): ResponseEntity<SuccessResponse> {
        memberService.changeSubscription(request)
        return ResponseEntity.ok(SuccessResponse())
    }

    @PostMapping("/unsubscription")
    fun cancelSubscription(@RequestBody request: UnsubscriptionRequest): ResponseEntity<SuccessResponse> {
        memberService.cancelSubscription(request)
        return ResponseEntity.ok(SuccessResponse())
    }

}
