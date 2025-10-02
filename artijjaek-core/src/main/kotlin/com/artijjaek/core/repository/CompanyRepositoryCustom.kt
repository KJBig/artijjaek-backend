package com.artijjaek.core.repository

import com.artijjaek.core.domain.Company
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CompanyRepositoryCustom {
    fun findAllByIs(companyIds: List<Long>): List<Company>
    fun findWithPageable(pageable: Pageable): Page<Company>
}