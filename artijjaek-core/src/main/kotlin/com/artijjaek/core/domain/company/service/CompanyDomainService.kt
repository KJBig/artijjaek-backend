package com.artijjaek.core.domain.company.service

import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.repository.CompanyRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    fun findWithPageable(pageable: Pageable): Page<Company> {
        return companyRepository.findWithPageable(pageable)
    }
}