package com.artijjaek.core.domain.category.repository

import com.artijjaek.core.domain.QCategory.category
import com.artijjaek.core.domain.category.entity.Category
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class CategoryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CategoryRepositoryCustom {

    override fun findByIdsOrAll(categoryIds: List<Long>): List<Category> {
        return jpaQueryFactory.selectFrom(category)
            .where(category.id.`in`(categoryIds))
            .fetch()
    }

    override fun findWithPageable(pageable: Pageable): Page<Category> {
        val content = jpaQueryFactory.selectFrom(category)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = jpaQueryFactory.selectFrom(category);

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetch().size.toLong()
        }
    }
}