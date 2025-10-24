package com.artijjaek.batch.dto

import com.artijjaek.core.domain.Article
import com.artijjaek.core.domain.Category

data class ArticleCategory(val article: Article, val category: Category)
