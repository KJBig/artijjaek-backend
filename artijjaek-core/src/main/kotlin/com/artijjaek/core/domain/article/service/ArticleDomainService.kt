package com.artijjaek.core.domain.article.service

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.repository.ArticleRepository
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.company.entity.Company
import org.springframework.stereotype.Service

@Service
class ArticleDomainService(
    private val articleRepository: ArticleRepository,
) {
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