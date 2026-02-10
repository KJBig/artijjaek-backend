package com.artijjaek.admin.controller

import com.artijjaek.admin.dto.common.SuccessDataResponse
import com.artijjaek.admin.dto.response.MemberOptionCategoryResponse
import com.artijjaek.admin.service.AdminCategoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/v1/category")
class AdminCategoryControllerV1(
    private val adminCategoryService: AdminCategoryService,
) {

    @GetMapping("/list")
    fun getMemberCategoryOptions(): ResponseEntity<SuccessDataResponse<List<MemberOptionCategoryResponse>>> {
        val response = adminCategoryService.getMemberCategoryOptions()
        return ResponseEntity.ok(SuccessDataResponse(response))
    }
}
