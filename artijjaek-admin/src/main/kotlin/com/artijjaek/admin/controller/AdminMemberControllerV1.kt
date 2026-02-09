package com.artijjaek.admin.controller

import com.artijjaek.admin.dto.common.SuccessDataResponse
import com.artijjaek.admin.dto.response.MemberDetailResponse
import com.artijjaek.admin.dto.response.MemberListPageResponse
import com.artijjaek.admin.enums.MemberListSearchType
import com.artijjaek.admin.enums.MemberListSortBy
import com.artijjaek.admin.enums.MemberStatusFilter
import com.artijjaek.admin.service.AdminMemberService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/v1/member")
class AdminMemberControllerV1(
    private val adminMemberService: AdminMemberService,
) {

    @GetMapping("/{memberId}")
    fun getMemberDetail(
        @PathVariable memberId: Long,
    ): ResponseEntity<SuccessDataResponse<MemberDetailResponse>> {
        val response = adminMemberService.getMemberDetail(memberId)
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

    @GetMapping("/list")
    fun getMembers(
        pageable: Pageable,
        @RequestParam(defaultValue = "ALL") status: MemberStatusFilter,
        @RequestParam(required = false) searchType: MemberListSearchType?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "SUBSCRIBE_DATE") sortBy: MemberListSortBy,
        @RequestParam(defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<SuccessDataResponse<MemberListPageResponse>> {
        val response = adminMemberService.searchMembers(
            pageable = pageable,
            statusFilter = status,
            searchType = searchType,
            keyword = keyword,
            sortBy = sortBy,
            sortDirection = sortDirection
        )
        return ResponseEntity.ok(SuccessDataResponse(response))
    }
}
