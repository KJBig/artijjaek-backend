package com.artijjaek.core.repository

import com.artijjaek.core.domain.Company
import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository : JpaRepository<Company, Long>, CompanyRepositoryCustom {

}