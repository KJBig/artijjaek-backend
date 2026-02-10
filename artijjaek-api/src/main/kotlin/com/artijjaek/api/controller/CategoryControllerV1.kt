package com.artijjaek.api.controller

import com.artijjaek.api.dto.common.PageResponse
import com.artijjaek.api.dto.common.SuccessDataResponse
import com.artijjaek.api.dto.response.CategorySimpleDataResponse
import com.artijjaek.api.service.CategoryService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/category")
@RestController
class CategoryControllerV1(
    private val categoryService: CategoryService,
) {

    @GetMapping("/list")
    fun getCategories(
        pageable: Pageable
    ): ResponseEntity<SuccessDataResponse<PageResponse<CategorySimpleDataResponse>>> {
        val response = categoryService.searchCategoryList(pageable)
        return ResponseEntity.ok(SuccessDataResponse(response))
    }

}