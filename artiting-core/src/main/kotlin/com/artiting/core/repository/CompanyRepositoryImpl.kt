package com.artiting.core.repository

import com.artiting.core.domain.Company
import com.artiting.core.domain.QCompany.company
import com.querydsl.jpa.impl.JPAQueryFactory


class CompanyRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CompanyRepositoryCustom {

    override fun findAllByIs(companyIds: List<Long>): List<Company> {
        return jpaQueryFactory.selectFrom(company)
            .where(company.id.`in`(companyIds))
            .fetch()
    }

}