package com.artijjaek.admin.dto.response

data class TopSubscribedCategoryResponse(
    val rank: Int,
    val categoryId: Long,
    val categoryName: String,
    val subscriberCount: Long,
)
