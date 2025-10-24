package com.artijjaek.api.service

import com.artijjaek.api.dto.common.PageResponse
import com.artijjaek.api.dto.response.CategorySimpleDataResponse
import com.artijjaek.core.domain.category.service.CategoryDomainService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryDomainService: CategoryDomainService,
) {

    @Transactional(readOnly = true)
    fun searchCategoryList(pageable: Pageable): PageResponse<CategorySimpleDataResponse> {
        val categoryPage = categoryDomainService.findWithPageable(pageable)
        val content = categoryPage.content.stream().map { CategorySimpleDataResponse.from(it) }.toList()
        return PageResponse(categoryPage.pageable.pageNumber, categoryPage.hasNext(), content)
    }

}