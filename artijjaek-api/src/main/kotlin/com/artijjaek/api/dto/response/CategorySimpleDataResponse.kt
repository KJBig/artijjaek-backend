package com.artijjaek.api.dto.response

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import com.artijjaek.core.domain.category.entity.Category

data class CategorySimpleDataResponse(
    val categoryId: Long,
    val categoryName: String,
) {
    companion object {
        fun from(category: Category): CategorySimpleDataResponse {
            return CategorySimpleDataResponse(
                categoryId = requireNotNull(category.id) { throw ApplicationException(ErrorCode.CATEGORY_ID_MISSING_ERROR) },
                categoryName = category.name
            )
        }
    }
}