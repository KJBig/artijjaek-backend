package com.artijjaek.core.domain.mail.dto

data class WelcomeMailPayload(
    val member: MemberSnapshot,
)

data class ArticleMailPayload(
    val member: MemberSnapshot,
    val articles: List<ArticleSnapshot>,
)

data class NoticeMailPayload(
    val member: MemberSnapshot,
    val title: String,
    val content: String,
)

data class MemberSnapshot(
    val email: String,
    val nickname: String,
    val uuidToken: String,
)

data class ArticleSnapshot(
    val title: String,
    val link: String,
    val companyNameKr: String,
    val companyLogo: String,
    val image: String?,
)
