package com.artijjaek.api.service

import com.artijjaek.api.dto.request.ChangeSubscribeRequest
import com.artijjaek.core.domain.Company
import com.artijjaek.core.domain.Subscribe
import com.artijjaek.core.enums.MemberStatus
import com.artijjaek.core.service.CompanyDomainService
import com.artijjaek.core.service.MemberDomainService
import com.artijjaek.core.service.SubscribeDomainService
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

    @Transactional
    fun chancelSubscribe(email: String, token: String) {
        val member = memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE)
            ?: throw IllegalStateException("Member Not Found.")

        if (member.uuidToken != token) {
            throw IllegalArgumentException("Token is not matched.")
        }

        member.changeMemberStatus(MemberStatus.DELETED)
    }

}