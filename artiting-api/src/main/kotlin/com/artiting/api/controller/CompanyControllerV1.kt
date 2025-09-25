package com.artiting.api.controller

import com.artiting.api.dto.common.SuccessDataResponse
import com.artiting.api.dto.response.CompanySimpleDataResponse
import com.artiting.api.service.CompanyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/company")
@RestController
class CompanyControllerV1(
    private val companyService: CompanyService,
) {

    @GetMapping("/list")
    fun getCompanyList(
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?,
    ): ResponseEntity<SuccessDataResponse<List<CompanySimpleDataResponse>>> {
        val response = companyService.searchCompanyList(page, size)
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

}