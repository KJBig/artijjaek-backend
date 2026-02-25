package com.artijjaek.admin.dto.request

data class PostArticleMailRequest(
    val memberIds: List<Long>,
    val articleIds: List<Long>,
)
