package com.artijjaek.core.domain.company.repository

import com.artijjaek.core.domain.company.entity.Company
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CompanyRepositoryCustom {
    fun findAllOrByIds(companyIds: List<Long>): List<Company>
    fun findWithPageable(pageable: Pageable): Page<Company>
}