package com.noati.core.service

import com.noati.core.domain.Company
import com.noati.core.repository.CompanyRepository
import org.springframework.stereotype.Service

@Service
class CompanyDomainService(
    private val companyRepository: CompanyRepository,
) {

    fun save(company: Company) {
        companyRepository.save(company)
    }
}