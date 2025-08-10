package com.noati.core.repository

import com.noati.core.domain.Article
import com.noati.core.domain.Company
import com.noati.core.domain.QArticle.article
import com.querydsl.jpa.impl.JPAQueryFactory


class ArticleRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ArticleRepositoryCustom {

    override fun findByCompanyRecent(company: Company, limit: Long): List<Article> {
        return jpaQueryFactory.selectFrom(article)
            .where(article.company.eq(company))
            .orderBy(article.createdAt.desc())
            .limit(limit)
            .fetch()
    }

}