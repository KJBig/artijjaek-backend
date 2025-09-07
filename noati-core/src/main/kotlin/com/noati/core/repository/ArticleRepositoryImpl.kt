package com.noati.core.repository

import com.noati.core.domain.Article
import com.noati.core.domain.Company
import com.noati.core.domain.QArticle.article
import com.querydsl.jpa.impl.JPAQueryFactory
import java.time.LocalDate


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

    override fun findYesterdayArticle(): List<Article> {
        val startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay()
        val startOfToday = LocalDate.now().atStartOfDay()

        return jpaQueryFactory.selectFrom(article)
            .where(
                article.createdAt.goe(startOfYesterday)
                    .and(article.createdAt.lt(startOfToday))
            )
            .orderBy(article.createdAt.desc())
            .fetch()
    }

    override fun findYesterdayByCompanies(memberSubscribeCompanies: List<Company>): List<Article> {
        val startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay()
        val startOfToday = LocalDate.now().atStartOfDay()
        val companyIds = memberSubscribeCompanies.stream().map { it.id }.toList()

        return jpaQueryFactory.selectFrom(article)
            .where(
                article.company.id.`in`(companyIds)
                    .and(
                        article.createdAt.goe(startOfYesterday)
                            .and(article.createdAt.lt(startOfToday))
                    )
            )
            .orderBy(article.id.desc())
            .fetch()
    }
}