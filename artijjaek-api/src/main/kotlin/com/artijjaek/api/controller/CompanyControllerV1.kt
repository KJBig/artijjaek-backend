package com.artijjaek.api.controller

import com.artijjaek.api.dto.common.PageResponse
import com.artijjaek.api.dto.common.SuccessDataResponse
import com.artijjaek.api.dto.response.CompanyCountResponse
import com.artijjaek.api.dto.response.CompanySimpleDataResponse
import com.artijjaek.api.service.CompanyService
import com.artijjaek.core.domain.company.enums.CompanySortOption
import org.springframework.data.domain.Pageable
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
    fun getCompanies(
        @RequestParam(name = "sort_option", defaultValue = "KR_NAME") sortOption: CompanySortOption,
        pageable: Pageable
    ): ResponseEntity<SuccessDataResponse<PageResponse<CompanySimpleDataResponse>>> {
        val response = companyService.searchCompanyList(sortOption, pageable)
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

    @GetMapping("/count")
    fun getCompanyCount(): ResponseEntity<SuccessDataResponse<CompanyCountResponse>> {
        val response = companyService.getCompanyCount()
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

}