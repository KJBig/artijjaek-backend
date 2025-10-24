package com.artijjaek.core.domain.category.repository

import com.artijjaek.core.domain.category.entity.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CategoryRepositoryCustom {
    fun findByIdsOrAll(categoryIds: List<Long>): List<Category>
    fun findWithPageable(pageable: Pageable): Page<Category>

}