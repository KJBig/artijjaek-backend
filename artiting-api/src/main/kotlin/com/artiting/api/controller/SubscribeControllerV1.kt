package com.artiting.api.controller

import com.artiting.api.dto.common.SuccessResponse
import com.artiting.api.dto.request.ChangeSubscribeRequest
import com.artiting.api.service.SubscribeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/subscribe")
@RestController
class SubscribeControllerV1(
    private val subscribeService: SubscribeService,
) {

    @PutMapping("/change")
    fun changeSubscribe(@RequestBody request: ChangeSubscribeRequest): ResponseEntity<SuccessResponse> {
        subscribeService.changeSubscribe(request)
        return ResponseEntity.ok(SuccessResponse())
    }

    @DeleteMapping("")
    fun cancelSubscribe(
        @RequestParam("email") email: String,
        @RequestParam("token") token: String
    ): ResponseEntity<SuccessResponse> {
        subscribeService.chancelSubscribe(email, token)
        return ResponseEntity.ok(SuccessResponse())
    }


}