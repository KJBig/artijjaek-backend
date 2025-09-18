package com.artiting.core.repository

import com.artiting.core.domain.Company

interface CompanyRepositoryCustom {
    fun findAllByIs(companyIds: List<Long>): List<Company>
}