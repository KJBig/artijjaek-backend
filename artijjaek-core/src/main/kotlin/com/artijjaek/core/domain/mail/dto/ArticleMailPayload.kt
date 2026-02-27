package com.artijjaek.core.domain.mail.dto

data class ArticleMailPayload(
    val member: MemberSnapshot,
    val articles: List<ArticleSnapshot>,
)
