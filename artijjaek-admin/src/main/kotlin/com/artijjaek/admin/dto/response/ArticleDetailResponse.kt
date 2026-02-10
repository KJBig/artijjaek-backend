package com.artijjaek.admin.dto.response

import java.time.LocalDateTime

data class ArticleDetailResponse(
    val articleId: Long,
    val title: String,
    val description: String?,
    val image: String?,
    val link: String,
    val company: ArticleCompanyResponse,
    val category: ArticleCategoryResponse?,
    val createdAt: LocalDateTime,
)
