package com.artijjaek.batch.dto

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.category.entity.Category

data class ArticleCategory(val article: Article, val category: Category)
