package com.artijjaek.core.domain.company.service

import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CompanySortOption
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

    fun findAllOrByIds(companyIds: List<Long>): List<Company> {
        return companyRepository.findAllOrByIds(companyIds)
    }

    fun findWithPageable(pageable: Pageable): Page<Company> {
        return companyRepository.findWithPageable(pageable)
    }

    fun findWithPageableOrderBySortOption(sortOption: CompanySortOption, pageable: Pageable): Page<Company> {
        return companyRepository.findWithPageableOrderBySortOption(sortOption, pageable)
    }

    fun findAll(): List<Company> {
        return companyRepository.findAll()
    }

    fun countCompanies(): Long {
        return companyRepository.count();
    }
}
