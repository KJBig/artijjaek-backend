package com.artijjaek.core.domain.category.service

import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.repository.CategoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CategoryDomainService(
    private val categoryRepository: CategoryRepository,
) {

    fun findAll(): List<Category> {
        return categoryRepository.findAll()
    }

    fun findByIdsOrAll(categoryIds: List<Long>): List<Category> {
        return categoryRepository.findByIdsOrAll(categoryIds)
    }

    fun findWithPageable(pageable: Pageable): Page<Category> {
        return categoryRepository.findWithPageable(pageable)
    }

}