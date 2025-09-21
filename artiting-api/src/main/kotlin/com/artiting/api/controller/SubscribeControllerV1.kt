package com.artiting.api.controller

import com.artiting.api.dto.common.SuccessResponse
import com.artiting.api.dto.request.ChangeSubscribeRequest
import com.artiting.api.service.SubscribeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

}