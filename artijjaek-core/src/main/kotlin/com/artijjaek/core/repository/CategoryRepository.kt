package com.artijjaek.core.repository

import com.artijjaek.core.domain.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
}