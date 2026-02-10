package com.artijjaek.admin.controller

import com.artijjaek.admin.dto.common.SuccessDataResponse
import com.artijjaek.admin.dto.response.MemberOptionCompanyResponse
import com.artijjaek.admin.service.AdminCompanyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/v1/company")
class AdminCompanyControllerV1(
    private val adminCompanyService: AdminCompanyService,
) {

    @GetMapping("/list")
    fun getMemberCompanyOptions(): ResponseEntity<SuccessDataResponse<List<MemberOptionCompanyResponse>>> {
        val response = adminCompanyService.getMemberCompanyOptions()
        return ResponseEntity.ok(SuccessDataResponse(response))
    }
}
