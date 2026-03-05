package com.artijjaek.admin.controller

import com.artijjaek.admin.dto.common.SuccessDataResponse
import com.artijjaek.admin.dto.common.SuccessResponse
import com.artijjaek.admin.dto.request.PostCompanyRequest
import com.artijjaek.admin.dto.request.PutCompanyRequest
import com.artijjaek.admin.dto.response.CompanyListPageResponse
import com.artijjaek.admin.dto.response.MemberOptionCompanyResponse
import com.artijjaek.admin.dto.response.PostCompanyResponse
import com.artijjaek.admin.dto.response.TopSubscribedCompanyResponse
import com.artijjaek.admin.service.AdminCompanyService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/subscribed/top")
    fun getTopSubscribedCompanies(): ResponseEntity<SuccessDataResponse<List<TopSubscribedCompanyResponse>>> {
        val response = adminCompanyService.getTopSubscribedCompanies()
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

    @GetMapping("/manage/list")
    fun getCompanies(
        pageable: Pageable,
        @RequestParam(required = false) keyword: String?,
    ): ResponseEntity<SuccessDataResponse<CompanyListPageResponse>> {
        val response = adminCompanyService.searchCompanies(
            pageable = pageable,
            keyword = keyword
        )
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

    @PostMapping
    fun postCompany(
        @RequestBody request: PostCompanyRequest,
    ): ResponseEntity<SuccessDataResponse<PostCompanyResponse>> {
        val response = adminCompanyService.createCompany(request)
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

    @PutMapping("/{companyId}")
    fun putCompany(
        @PathVariable companyId: Long,
        @RequestBody request: PutCompanyRequest,
    ): ResponseEntity<SuccessResponse> {
        adminCompanyService.updateCompany(companyId, request)
        return ResponseEntity.ok(SuccessResponse())
    }
}
