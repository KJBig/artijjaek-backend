package com.artijjaek.api.service

import com.artijjaek.api.common.UuidTokenGenerator
import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.dto.request.SubscriptionChangeRequest
import com.artijjaek.api.dto.request.UnsubscriptionRequest
import com.artijjaek.api.dto.response.CategorySimpleDataResponse
import com.artijjaek.api.dto.response.CompanySimpleDataResponse
import com.artijjaek.api.dto.response.MemberDataResponse
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.*
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.common.mail.service.MailService
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
import com.artijjaek.core.domain.unsubscription.entity.Unsubscription
import com.artijjaek.core.domain.unsubscription.service.UnsubscriptionDomainService
import com.artijjaek.core.webhook.WebHookService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberDomainService: MemberDomainService,
    private val companyDomainService: CompanyDomainService,
    private val companySubscriptionDomainService: CompanySubscriptionDomainService,
    private val categoryDomainService: CategoryDomainService,
    private val categorySubscriptionDomainService: CategorySubscriptionDomainService,
    private val unsubscriptionDomainService: UnsubscriptionDomainService,
    private val mailService: MailService,
    private val webHookService: WebHookService,
) {

    @Transactional
    fun register(request: RegisterMemberRequest) {
        memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
            ?.let { throw ApplicationException(MEMBER_DUPLICATE_ERROR) }

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
        val companies: List<Company> = companyDomainService.findAllOrByIds(request.companyIds)
        val companySubscriptions = companies.map { CompanySubscription(member = newMember, company = it) }
        companySubscriptionDomainService.saveAll(companySubscriptions)

        // 구독 카테고리
        val categories: List<Category> = categoryDomainService.findAllOrByIds(request.categoryIds)
        val categorySubscriptions = categories.map { CategorySubscription(member = newMember, category = it) }
        categorySubscriptionDomainService.saveAll(categorySubscriptions)

        mailService.sendSubscribeMail(MemberAlertDto.from(newMember))
        webHookService.sendNewSubscribeMessage(newMember)
    }

    @Transactional(readOnly = true)
    fun getMemberDataWithToken(email: String, token: String): MemberDataResponse {
        val member = memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE)
            ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

        if (member.uuidToken != token) {
            throw ApplicationException(MEMBER_TOKEN_NOT_MATCH_ERROR)
        }

        val companyIds = companySubscriptionDomainService.findAllByMemberFetchCompany(member)
            .mapNotNull { companySubscription -> CompanySimpleDataResponse.from(companySubscription.company) }
        val categoryIds = categorySubscriptionDomainService.findAllByMemberFetchCategory(member)
            .mapNotNull { categorySubscription -> CategorySimpleDataResponse.from(categorySubscription.category) }

        return MemberDataResponse.of(member, companyIds, categoryIds)
    }

    @Transactional
    fun changeSubscription(request: SubscriptionChangeRequest) {
        val member = memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
            ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

        if (member.uuidToken != request.token) {
            throw ApplicationException(MEMBER_TOKEN_NOT_MATCH_ERROR)
        }

        member.changeNickname(request.nickname)

        // 구독 회사 변경
        companySubscriptionDomainService.deleteAllByMemberId(member.id!!)
        val companies: List<Company> = companyDomainService.findAllOrByIds(request.companyIds)
        val companySubscriptions = companies.map { CompanySubscription(member = member, company = it) }
        companySubscriptionDomainService.saveAll(companySubscriptions)

        // 구독 카테고리 변경
        categorySubscriptionDomainService.deleteAllByMemberId(member.id!!)
        val categories: List<Category> = categoryDomainService.findAllOrByIds(request.categoryIds)
        val categorySubscriptions = categories.map { CategorySubscription(member = member, category = it) }
        categorySubscriptionDomainService.saveAll(categorySubscriptions)
    }

    @Transactional
    fun cancelSubscription(request: UnsubscriptionRequest) {
        val member = memberDomainService.findByEmailAndMemberStatus(request.email, MemberStatus.ACTIVE)
            ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

        if (member.uuidToken != request.token) {
            throw ApplicationException(MEMBER_TOKEN_NOT_MATCH_ERROR)
        }

        val unsubscription = Unsubscription(
            member = member,
            email = member.email,
            reason = request.reason,
            detail = request.detail
        )
        unsubscriptionDomainService.saveUnsubscription(unsubscription)

        member.changeMemberStatus(MemberStatus.DELETED)
        member.changeEmail(null)

        webHookService.sendUnsubscribeMessage(member, unsubscription)
    }


}