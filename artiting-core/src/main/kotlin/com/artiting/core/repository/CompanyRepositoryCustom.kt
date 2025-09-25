package com.artiting.core.repository

import com.artiting.core.domain.Company
import org.springframework.data.domain.Pageable

interface CompanyRepositoryCustom {
    fun findAllByIs(companyIds: List<Long>): List<Company>
    fun findWithPageable(pageable: Pageable): List<Company>
}