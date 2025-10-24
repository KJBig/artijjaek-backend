package com.artijjaek.core.domain.category.repository

import com.artijjaek.core.domain.category.entity.Category

interface CategoryRepositoryCustom {
    fun findByIdsOrAll(categoryIds: List<Long>): List<Category>
}