package com.artijjaek.batch.dto

data class CrawledArticleDto(
    val title: String,
    val link: String,
    val firstText: String?,
    val firstImg: String?,
)
