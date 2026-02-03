package com.artijjaek.core.domain.article.repository

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.company.entity.Company

interface ArticleRepositoryCustom {
    fun findByCompanyRecent(company: Company, limit: Long): List<Article>
    fun findTodayArticle(): List<Article>
    fun findTodayByCompaniesAndCategories(companies: List<Company>, categories: List<Category>): List<Article>
    fun allocateCategory(targetArticle: Article, category: Category)
    fun findExistByUrls(company: Company, articleUrls: List<String>): List<Article>
}