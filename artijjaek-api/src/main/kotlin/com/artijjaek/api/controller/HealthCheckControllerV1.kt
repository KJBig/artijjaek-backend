package com.artijjaek.api.controller

import com.artijjaek.api.dto.common.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class HealthCheckControllerV1 {

    @GetMapping("/health")
    fun checkHealth(): ResponseEntity<SuccessResponse> {
        return ResponseEntity.ok(SuccessResponse())
    }

}