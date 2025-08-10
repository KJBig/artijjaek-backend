package com.noati.core.repository

import com.noati.core.domain.Article
import com.noati.core.domain.Company

interface ArticleRepositoryCustom {
    fun findByCompanyRecent(company: Company, limit: Long): List<Article>
}