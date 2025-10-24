package com.artijjaek.core.domain.company.repository

import com.artijjaek.core.domain.company.entity.Company
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository : JpaRepository<Company, Long>, CompanyRepositoryCustom {

}