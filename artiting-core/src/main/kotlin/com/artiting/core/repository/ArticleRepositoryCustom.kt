package com.artiting.core.repository

import com.artiting.core.domain.Article
import com.artiting.core.domain.Company

interface ArticleRepositoryCustom {
    fun findByCompanyRecent(company: Company, limit: Long): List<Article>
    fun findYesterdayArticle(): List<Article>
    fun findYesterdayByCompanies(memberSubscribeCompanies: List<Company>): List<Article>
}