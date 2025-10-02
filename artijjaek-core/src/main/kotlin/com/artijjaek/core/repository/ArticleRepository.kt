package com.artijjaek.core.repository

import com.artijjaek.core.domain.Article
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Long>, ArticleRepositoryCustom {
}