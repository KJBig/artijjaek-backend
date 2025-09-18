package com.artiting.api.service

import com.artiting.api.dto.request.RegisterMemberRequest
import com.artiting.core.domain.Company
import com.artiting.core.domain.Member
import com.artiting.core.domain.Subscribe
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
        val newMember = memberDomainService.save(Member(email = request.email, nickname = request.nickname))
        val companies: List<Company> = companyDomainService.findByIdsOrAll(request.companyIds)

        val subscribes = companies.stream()
            .map { Subscribe(member = newMember, company = it) }
            .toList()
        subscribeDomainService.saveAll(subscribes)
    }

}