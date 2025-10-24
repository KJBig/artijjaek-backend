package com.artijjaek.api.controller

import com.artijjaek.api.dto.common.SuccessResponse
import com.artijjaek.api.dto.request.SubscriptionChangeRequest
import com.artijjaek.api.service.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/subscription")
@RestController
class SubscriptionControllerV1(
    private val subscriptionService: SubscriptionService,
) {

    @PutMapping("/change")
    fun changeSubscription(@RequestBody request: SubscriptionChangeRequest): ResponseEntity<SuccessResponse> {
        subscriptionService.changeSubscription(request)
        return ResponseEntity.ok(SuccessResponse())
    }

    @DeleteMapping("")
    fun cancelSubscription(
        @RequestParam("email") email: String,
        @RequestParam("token") token: String
    ): ResponseEntity<SuccessResponse> {
        subscriptionService.chancelSubscription(email, token)
        return ResponseEntity.ok(SuccessResponse())
    }


}