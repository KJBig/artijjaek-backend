package com.artijjaek.admin.controller

import com.artijjaek.admin.dto.common.SuccessDataResponse
import com.artijjaek.admin.dto.response.ArticleListPageResponse
import com.artijjaek.admin.enums.ArticleListSortBy
import com.artijjaek.admin.service.AdminArticleService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/v1/article")
class AdminArticleControllerV1(
    private val adminArticleService: AdminArticleService,
) {

    @GetMapping("/list")
    fun getArticles(
        pageable: Pageable,
        @RequestParam(required = false) companyId: Long?,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) title: String?,
        @RequestParam(defaultValue = "REGISTER_DATE") sortBy: ArticleListSortBy,
        @RequestParam(defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<SuccessDataResponse<ArticleListPageResponse>> {
        val response = adminArticleService.searchArticles(
            pageable = pageable,
            companyId = companyId,
            categoryId = categoryId,
            title = title,
            sortBy = sortBy,
            sortDirection = sortDirection
        )
        return ResponseEntity.ok(SuccessDataResponse(response))
    }
}
