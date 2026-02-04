package com.artijjaek.core.domain.category.repository

import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.entity.QCategory.category
import com.artijjaek.core.domain.category.enums.PublishType
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class CategoryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CategoryRepositoryCustom {

    override fun findByIdsOrAll(categoryIds: List<Long>): List<Category> {
        return jpaQueryFactory.selectFrom(category)
            .where(category.id.`in`(categoryIds).takeIf { categoryIds.isNotEmpty() })
            .fetch()
    }

    override fun findPublishCategoryWithPageable(pageable: Pageable): Page<Category> {
        val content = jpaQueryFactory.selectFrom(category)
            .where(category.publishType.eq(PublishType.PUBLISH))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = jpaQueryFactory.selectFrom(category)
            .where(category.publishType.eq(PublishType.PUBLISH));

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetch().size.toLong()
        }
    }
}