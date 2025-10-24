package com.artijjaek.core.domain.category.repository

import com.artijjaek.core.domain.category.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>, CategoryRepositoryCustom {
}