package com.artijjaek.core.domain.category.service

import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryDomainService(
    private val categoryRepository: CategoryRepository,
) {

    fun findAll(): List<Category> {
        return categoryRepository.findAll()
    }

}