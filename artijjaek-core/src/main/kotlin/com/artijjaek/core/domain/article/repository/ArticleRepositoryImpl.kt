package com.artijjaek.core.domain.article.repository

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.entity.QArticle.article
import com.artijjaek.core.domain.article.enums.ArticleSortBy
import com.artijjaek.core.domain.category.entity.QCategory.category
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.company.entity.QCompany.company
import com.artijjaek.core.domain.company.entity.Company
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.support.PageableExecutionUtils


class ArticleRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ArticleRepositoryCustom {

    override fun findWithCondition(
        pageable: Pageable,
        companyId: Long?,
        categoryId: Long?,
        titleKeyword: String?,
        sortBy: ArticleSortBy,
        sortDirection: Sort.Direction,
    ): Page<Article> {
        val orderSpecifiers = when (sortBy) {
            ArticleSortBy.CREATED_AT -> arrayOf(createdAtOrder(sortDirection), idOrder(sortDirection))
            ArticleSortBy.TITLE -> arrayOf(titleOrder(sortDirection), idOrder(sortDirection))
            ArticleSortBy.COMPANY -> arrayOf(companyNameOrder(sortDirection), idOrder(sortDirection))
            ArticleSortBy.CATEGORY -> arrayOf(categoryNameOrder(sortDirection), idOrder(sortDirection))
        }

        val content = jpaQueryFactory.selectFrom(article)
            .leftJoin(article.company, company).fetchJoin()
            .leftJoin(article.category, category).fetchJoin()
            .where(
                companyIdEq(companyId),
                categoryIdEq(categoryId),
                titleContains(titleKeyword)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*orderSpecifiers)
            .fetch()

        val countQuery = jpaQueryFactory.select(article.id.count())
            .from(article)
            .where(
                companyIdEq(companyId),
                categoryIdEq(categoryId),
                titleContains(titleKeyword)
            )

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    override fun findByCompanyRecent(company: Company, limit: Long): List<Article> {
        return jpaQueryFactory.selectFrom(article)
            .where(article.company.id.eq(company.id))
            .limit(limit)
            .orderBy(article.id.desc())
            .fetch()
    }

    override fun findTodayArticle(): List<Article> {
        val startOfToday = LocalDate.now().atStartOfDay()

        return jpaQueryFactory.selectFrom(article)
            .where(
                article.createdAt.goe(startOfToday)
                    .and(article.createdAt.lt(LocalDateTime.now()))
            )
            .orderBy(article.createdAt.desc())
            .fetch()
    }

    override fun findTodayByCompaniesAndCategories(
        companies: List<Company>,
        categories: List<Category>
    ): List<Article> {
        val startOfToday = LocalDate.now().atStartOfDay()
        val companyIds = companies.stream().map { it.id }.toList()
        val categoryIds = categories.stream().map { it.id }.toList()

        return jpaQueryFactory.selectFrom(article)
            .where(
                article.company.id.`in`(companyIds)
                    .and(article.category.id.`in`(categoryIds))
                    .and(
                        article.createdAt.goe(startOfToday)
                            .and(article.createdAt.lt(LocalDateTime.now()))
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

    override fun findExistByUrls(company: Company, articleUrls: List<String>): List<Article> {
        return jpaQueryFactory.selectFrom(article)
            .where(
                article.company.id.eq(company.id)
                    .and(article.link.`in`(articleUrls))
            )
            .fetch()
    }

    private fun companyIdEq(companyId: Long?): BooleanExpression? {
        return companyId?.let { article.company.id.eq(it) }
    }

    private fun categoryIdEq(categoryId: Long?): BooleanExpression? {
        return categoryId?.let { article.category.id.eq(it) }
    }

    private fun titleContains(titleKeyword: String?): BooleanExpression? {
        return titleKeyword?.takeIf { it.isNotBlank() }?.let { article.title.contains(it) }
    }

    private fun createdAtOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        return if (sortDirection.isAscending) article.createdAt.asc() else article.createdAt.desc()
    }

    private fun titleOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        return if (sortDirection.isAscending) article.title.asc() else article.title.desc()
    }

    private fun companyNameOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        return if (sortDirection.isAscending) article.company.nameKr.asc() else article.company.nameKr.desc()
    }

    private fun categoryNameOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        return if (sortDirection.isAscending) article.category.name.asc() else article.category.name.desc()
    }

    private fun idOrder(sortDirection: Sort.Direction): OrderSpecifier<*> {
        val order = if (sortDirection.isAscending) Order.ASC else Order.DESC
        return OrderSpecifier(order, article.id)
    }
}
