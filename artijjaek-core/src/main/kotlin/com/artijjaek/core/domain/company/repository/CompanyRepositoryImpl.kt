package com.artijjaek.core.domain.company.repository

import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.entity.QCompany.company
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils


class CompanyRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CompanyRepositoryCustom {

    override fun findAllByIs(companyIds: List<Long>): List<Company> {
        return jpaQueryFactory.selectFrom(company)
            .where(company.id.`in`(companyIds))
            .fetch()
    }

    override fun findWithPageable(pageable: Pageable): Page<Company> {
        val content = jpaQueryFactory.selectFrom(company)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(company.id.asc())
            .fetch()

        val countQuery = jpaQueryFactory.selectFrom(company)
            .orderBy(company.id.asc())

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetch().size.toLong()
        }
    }

}