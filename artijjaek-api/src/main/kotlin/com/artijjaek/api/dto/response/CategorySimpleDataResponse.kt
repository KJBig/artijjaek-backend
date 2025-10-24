package com.artijjaek.api.dto.response

import com.artijjaek.core.domain.category.entity.Category

data class CategorySimpleDataResponse(
    val categoryId: Long,
    val categoryName: String,
) {
    companion object {
        fun from(category: Category): CategorySimpleDataResponse {
            return CategorySimpleDataResponse(
                categoryId = requireNotNull(category.id) { "Category ID must not be null" },
                categoryName = category.name
            )
        }
    }
}