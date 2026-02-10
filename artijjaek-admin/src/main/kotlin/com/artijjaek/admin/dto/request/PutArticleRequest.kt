package com.artijjaek.admin.dto.request

data class PutArticleRequest(
    val title: String,
    val description: String?,
    val image: String?,
    val link: String,
    val companyId: Long,
    val categoryId: Long?,
)
