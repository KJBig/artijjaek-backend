package com.artiting.core.service

import com.artiting.core.domain.Article
import com.artiting.core.domain.Company
import com.artiting.core.repository.ArticleRepository
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
}