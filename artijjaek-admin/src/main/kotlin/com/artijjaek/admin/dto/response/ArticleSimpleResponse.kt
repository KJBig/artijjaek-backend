package com.artijjaek.admin.dto.response

import java.time.LocalDateTime

data class ArticleSimpleResponse(
    val articleId: Long,
    val title: String,
    val company: ArticleCompanyResponse,
    val categoryName: String?,
    val link: String,
    val image: String?,
    val description: String?,
    val createdAt: LocalDateTime,
)
