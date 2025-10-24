package com.artijjaek.core.domain.article.repository

import com.artijjaek.core.domain.article.entity.Article
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Long>, ArticleRepositoryCustom {
}