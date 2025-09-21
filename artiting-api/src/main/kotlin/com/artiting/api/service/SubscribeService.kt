package com.artiting.api.service

import com.artiting.api.dto.request.ChangeSubscribeRequest
import com.artiting.core.domain.Company
import com.artiting.core.domain.Subscribe
import com.artiting.core.enums.MemberStatus
import com.artiting.core.service.CompanyDomainService
import com.artiting.core.service.MemberDomainService
import com.artiting.core.service.SubscribeDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubscribeService(
    private val memberDomainService: MemberDomainService,
    private val companyDomainService: CompanyDomainService,
    private val subscribeDomainService: SubscribeDomainService,
) {

    @Transactional
    fun changeSubscribe(request: ChangeSubscribeRequest) {
        val member = memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
            ?: throw IllegalStateException("Member Not Found.")

        if (member.uuidToken != request.token) {
            throw IllegalArgumentException("Token is not matched.")
        }

        subscribeDomainService.deleteAllByMemberId(member.id!!)

        val companies: List<Company> = companyDomainService.findByIdsOrAll(request.companyIds)

        val subscribes = companies.map { Subscribe(member = member, company = it) }
        subscribeDomainService.saveAll(subscribes)
    }

}