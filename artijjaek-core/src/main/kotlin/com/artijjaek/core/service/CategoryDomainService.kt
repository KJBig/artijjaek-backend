package com.artijjaek.core.service

import com.artijjaek.core.domain.Category
import com.artijjaek.core.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryDomainService(
    private val categoryRepository: CategoryRepository,
) {

    fun findAll(): List<Category> {
        return categoryRepository.findAll()
    }

}