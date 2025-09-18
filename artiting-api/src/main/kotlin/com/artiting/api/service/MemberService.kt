package com.artiting.api.service

import com.artiting.api.common.UuidTokenGenerator
import com.artiting.api.dto.request.RegisterMemberRequest
import com.artiting.core.domain.Company
import com.artiting.core.domain.Member
import com.artiting.core.domain.Subscribe
import com.artiting.core.enums.MemberStatus
import com.artiting.core.service.CompanyDomainService
import com.artiting.core.service.MemberDomainService
import com.artiting.core.service.SubscribeDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberDomainService: MemberDomainService,
    private val companyDomainService: CompanyDomainService,
    private val subscribeDomainService: SubscribeDomainService,
) {

    @Transactional
    fun register(request: RegisterMemberRequest) {
        memberDomainService.findByEmail(request.email)
            ?.let { throw IllegalStateException("This email already exists.") }

        val memberToken = UuidTokenGenerator.generatorUuidToken()

        val newMember = memberDomainService.save(
            Member(
                email = request.email,
                nickname = request.nickname,
                uuidToken = memberToken,
                memberStatus = MemberStatus.ACTIVE
            )
        )
        val companies: List<Company> = companyDomainService.findByIdsOrAll(request.companyIds)

        val subscribes = companies.map { Subscribe(member = newMember, company = it) }
        subscribeDomainService.saveAll(subscribes)
    }

}