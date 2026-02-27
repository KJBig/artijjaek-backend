package com.artijjaek.core.domain.mail.dto

data class ArticleSnapshot(
    val title: String,
    val link: String,
    val companyNameKr: String,
    val companyLogo: String,
    val image: String?,
)
