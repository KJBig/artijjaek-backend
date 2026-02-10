package com.artijjaek.core.domain.article.repository

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.enums.ArticleSortBy
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.company.entity.Company
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface ArticleRepositoryCustom {
    fun findWithCondition(
        pageable: Pageable,
        companyId: Long?,
        categoryId: Long?,
        titleKeyword: String?,
        sortBy: ArticleSortBy,
        sortDirection: Sort.Direction,
    ): Page<Article>
    fun findByCompanyRecent(company: Company, limit: Long): List<Article>
    fun findTodayArticle(): List<Article>
    fun findTodayByCompaniesAndCategories(companies: List<Company>, categories: List<Category>): List<Article>
    fun allocateCategory(targetArticle: Article, category: Category)
    fun findExistByUrls(company: Company, articleUrls: List<String>): List<Article>
}
