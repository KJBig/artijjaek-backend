package com.noati.core.service

import com.noati.core.domain.Article
import com.noati.core.repository.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleDomainService(
    private val articleRepository: ArticleRepository,
) {
    fun findYesterdayArticle(): List<Article> {
        return articleRepository.findYesterdayArticle()
    }
}