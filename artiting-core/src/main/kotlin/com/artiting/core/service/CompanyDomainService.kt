package com.artiting.core.service

import com.artiting.core.domain.Company
import com.artiting.core.repository.CompanyRepository
import org.springframework.stereotype.Service

@Service
class CompanyDomainService(
    private val companyRepository: CompanyRepository,
) {

    fun save(company: Company) {
        companyRepository.save(company)
    }

    fun findByIdsOrAll(companyIds: List<Long>): List<Company> {
        if (companyIds.isEmpty()) {
            return companyRepository.findAll()
        }
        return companyRepository.findAllByIs(companyIds)
    }
}