package com.artijjaek.core.domain.article.service

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.enums.ArticleSortBy
import com.artijjaek.core.domain.article.repository.ArticleRepository
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.company.entity.Company
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ArticleDomainService(
    private val articleRepository: ArticleRepository,
) {
    fun findWithCondition(
        pageable: Pageable,
        companyId: Long?,
        categoryId: Long?,
        titleKeyword: String?,
        sortBy: ArticleSortBy,
        sortDirection: Sort.Direction,
    ): Page<Article> {
        return articleRepository.findWithCondition(
            pageable = pageable,
            companyId = companyId,
            categoryId = categoryId,
            titleKeyword = titleKeyword,
            sortBy = sortBy,
            sortDirection = sortDirection
        )
    }

    fun findTodayArticle(): List<Article> {
        return articleRepository.findTodayArticle()
    }

    fun save(article: Article) {
        articleRepository.save(article)
    }

    fun findByCompanyRecent(company: Company, size: Long): List<Article> {
        return articleRepository.findByCompanyRecent(company, size)
    }

    fun findTodayByCompaniesAndCategories(companies: List<Company>, categories: List<Category>): List<Article> {
        return articleRepository.findTodayByCompaniesAndCategories(companies, categories)
    }

    fun allocateCategory(article: Article, category: Category) {
        articleRepository.allocateCategory(article, category);
    }

    fun findExistByUrls(company: Company, articleUrls: List<String>): List<Article> {
        return articleRepository.findExistByUrls(company, articleUrls)
    }
}
