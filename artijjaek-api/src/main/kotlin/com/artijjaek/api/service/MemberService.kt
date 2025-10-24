package com.artijjaek.api.service

import com.artijjaek.api.common.UuidTokenGenerator
import com.artijjaek.api.dto.request.CheckMemberTokenAvailabilityRequest
import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.dto.response.MemberTokenAvailabilityResponse
import com.artijjaek.core.domain.Company
import com.artijjaek.core.domain.CompanySubscription
import com.artijjaek.core.domain.Member
import com.artijjaek.core.enums.MemberStatus
import com.artijjaek.core.service.CompanyDomainService
import com.artijjaek.core.service.CompanySubscriptionDomainService
import com.artijjaek.core.service.MemberDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberDomainService: MemberDomainService,
    private val companyDomainService: CompanyDomainService,
    private val companySubscriptionDomainService: CompanySubscriptionDomainService,
) {

    @Transactional
    fun register(request: RegisterMemberRequest) {
        memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
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

        val companySubscriptions = companies.map { CompanySubscription(member = newMember, company = it) }
        companySubscriptionDomainService.saveAll(companySubscriptions)
    }

    @Transactional(readOnly = true)
    fun checkTokenAvailability(request: CheckMemberTokenAvailabilityRequest): MemberTokenAvailabilityResponse {
        val member = memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
            ?: throw IllegalStateException("Member Not Found.")
        val isAvailable = member.uuidToken == request.token
        return MemberTokenAvailabilityResponse(isAvailable)
    }

}