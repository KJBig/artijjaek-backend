package com.artiting.api.controller

import com.artiting.api.dto.common.SuccessResponse
import com.artiting.api.dto.request.RegisterMemberRequest
import com.artiting.api.service.MemberService
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

}