package com.artijjaek.core.domain.article.repository

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.company.entity.Company

interface ArticleRepositoryCustom {
    fun findByCompanyRecent(company: Company, limit: Long): List<Article>
    fun findYesterdayArticle(): List<Article>
    fun findYesterdayByCompanies(memberSubscribeCompanies: List<Company>): List<Article>
    fun allocateCategory(targetArticle: Article, category: Category)
}