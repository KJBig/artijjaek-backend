package com.artijjaek.admin.dto.request

data class PostNoticeMailRequest(
    val memberIds: List<Long>,
    val title: String,
    val content: String,
)
