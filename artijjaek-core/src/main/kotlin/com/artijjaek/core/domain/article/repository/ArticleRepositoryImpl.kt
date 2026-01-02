package com.artijjaek.core.domain.article.repository

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.entity.QArticle.article
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.company.entity.Company
import com.querydsl.jpa.impl.JPAQueryFactory
import java.time.LocalDate
import java.time.LocalDateTime


class ArticleRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ArticleRepositoryCustom {

    override fun findByCompanyRecent(company: Company, limit: Long): List<Article> {
        return jpaQueryFactory.selectFrom(article)
            .where(article.company.id.eq(company.id))
            .limit(limit)
            .orderBy(article.id.desc())
            .fetch()
    }

    override fun findYesterdayArticle(): List<Article> {
//        val startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay()
        val startOfToday = LocalDate.now().atStartOfDay()

        return jpaQueryFactory.selectFrom(article)
            .where(
                article.createdAt.goe(startOfToday)
                    .and(article.createdAt.lt(LocalDateTime.now()))
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

    override fun allocateCategory(targetArticle: Article, category: Category) {
        jpaQueryFactory.update(article)
            .set(article.category, category)
            .where(article.id.eq(targetArticle.id))
            .execute()
    }
}