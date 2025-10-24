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
    fun findYesterdayArticle(): List<Article> {
        return articleRepository.findYesterdayArticle()
    }

    fun save(article: Article) {
        articleRepository.save(article)
    }

    fun findByCompanyRecent(company: Company, size: Long): List<Article> {
        return articleRepository.findByCompanyRecent(company, size)
    }

    fun findYesterdayByCompanies(memberSubscribeCompanies: List<Company>): List<Article> {
        return articleRepository.findYesterdayByCompanies(memberSubscribeCompanies)
    }

    fun allocateCategory(article: Article, category: Category) {
        articleRepository.allocateCategory(article, category);
    }
}