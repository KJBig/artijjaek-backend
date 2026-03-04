package com.artijjaek.core.domain.subscription.dto

data class TopSubscribedCategoryCount(
    val categoryId: Long,
    val categoryName: String,
    val subscriberCount: Long,
)
