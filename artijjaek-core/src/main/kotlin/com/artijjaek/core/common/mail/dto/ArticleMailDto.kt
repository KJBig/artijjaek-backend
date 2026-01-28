package com.artijjaek.core.common.mail.dto

import com.artijjaek.core.domain.article.entity.Article

data class ArticleMailDto(
    val title: String,
    val link: String,
    val image: String?,
    val companyLogo: String,
    val companyNameKr: String
) {

    companion object {
        fun from(article: Article): ArticleMailDto {
            return ArticleMailDto(
                title = article.title,
                link = article.link,
                image = article.image,
                companyLogo = article.company.logo,
                companyNameKr = article.company.nameKr
            )
        }
    }
}
