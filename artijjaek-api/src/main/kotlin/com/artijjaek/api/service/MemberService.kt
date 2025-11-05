package com.artijjaek.api.service

import com.artijjaek.api.common.UuidTokenGenerator
import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.dto.request.SubscriptionChangeRequest
import com.artijjaek.api.dto.request.UnsubscriptionRequest
import com.artijjaek.api.dto.response.MemberDataResponse
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberDomainService
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.service.CategorySubscriptionDomainService
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberDomainService: MemberDomainService,
    private val companyDomainService: CompanyDomainService,
    private val companySubscriptionDomainService: CompanySubscriptionDomainService,
    private val categoryDomainService: CategoryDomainService,
    private val categorySubscriptionDomainService: CategorySubscriptionDomainService,
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

        // 구독 회사
        val companies: List<Company> = companyDomainService.findByIdsOrAll(request.companyIds)
        val companySubscriptions = companies.map { CompanySubscription(member = newMember, company = it) }
        companySubscriptionDomainService.saveAll(companySubscriptions)

        // 구독 카테고리
        val categories: List<Category> = categoryDomainService.findByIdsOrAll(request.categoryIds)
        val categorySubscriptions = categories.map { CategorySubscription(member = newMember, category = it) }
        categorySubscriptionDomainService.saveAll(categorySubscriptions)

    }

    @Transactional(readOnly = true)
    fun getMemberDataWithToken(email: String, token: String): MemberDataResponse {
        val member = memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE)
            ?: throw IllegalStateException("Member Not Found.")

        if (member.uuidToken != token) {
            throw IllegalArgumentException("Token Not Matched")
        }

        val companyIds = companySubscriptionDomainService.findAllByMember(member)
            .mapNotNull { companySubscription -> companySubscription.company.id }
        val categoryIds: List<Long> = categorySubscriptionDomainService.findAllByMember(member)
            .mapNotNull { categorySubscription -> categorySubscription.category.id }

        return MemberDataResponse.of(member, companyIds, categoryIds)
    }

    @Transactional
    fun changeSubscription(request: SubscriptionChangeRequest) {
        val member = memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
            ?: throw IllegalStateException("Member Not Found.")

        if (member.uuidToken != request.token) {
            throw IllegalArgumentException("Token is not matched.")
        }

        // 구독 회사 변경
        companySubscriptionDomainService.deleteAllByMemberId(member.id!!)
        val companies: List<Company> = companyDomainService.findByIdsOrAll(request.companyIds)
        val companySubscriptions = companies.map { CompanySubscription(member = member, company = it) }
        companySubscriptionDomainService.saveAll(companySubscriptions)

        // 구독 카테고리 변경
        categorySubscriptionDomainService.deleteAllByMemberId(member.id!!)
        val categories: List<Category> = categoryDomainService.findByIdsOrAll(request.categoryIds)
        val categorySubscriptions = categories.map { CategorySubscription(member = member, category = it) }
        categorySubscriptionDomainService.saveAll(categorySubscriptions)
    }

    @Transactional
    fun cancelSubscription(request: UnsubscriptionRequest) {
        val member = memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
            ?: throw IllegalStateException("Member Not Found.")

        if (member.uuidToken != request.token) {
            throw IllegalArgumentException("Token is not matched.")
        }

        member.changeMemberStatus(MemberStatus.DELETED)
    }

}