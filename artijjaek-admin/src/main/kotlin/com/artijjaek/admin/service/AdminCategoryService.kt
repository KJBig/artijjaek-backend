package com.artijjaek.admin.service

import com.artijjaek.admin.dto.response.MemberOptionCategoryResponse
import com.artijjaek.core.domain.category.service.CategoryDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminCategoryService(
    private val categoryDomainService: CategoryDomainService,
) {

    @Transactional(readOnly = true)
    fun getMemberCategoryOptions(): List<MemberOptionCategoryResponse> {
        return categoryDomainService.findAll().map {
            MemberOptionCategoryResponse(
                categoryId = it.id!!,
                categoryName = it.name
            )
        }
    }
}
