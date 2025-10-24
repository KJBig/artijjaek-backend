package com.artijjaek.core.domain.category.repository

import com.artijjaek.core.domain.QCategory.category
import com.artijjaek.core.domain.category.entity.Category
import com.querydsl.jpa.impl.JPAQueryFactory

class CategoryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CategoryRepositoryCustom {

    override fun findByIdsOrAll(categoryIds: List<Long>): List<Category> {
        return jpaQueryFactory.selectFrom(category)
            .where(category.id.`in`(categoryIds))
            .fetch()
    }

}