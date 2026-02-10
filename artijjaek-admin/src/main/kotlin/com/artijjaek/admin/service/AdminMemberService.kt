package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PatchMemberStatusRequest
import com.artijjaek.admin.dto.request.PutMemberRequest
import com.artijjaek.admin.dto.response.*
import com.artijjaek.admin.enums.MemberListSearchType
import com.artijjaek.admin.enums.MemberListSortBy
import com.artijjaek.admin.enums.MemberStatusFilter
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.*
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.member.enums.MemberSortBy
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberDomainService
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.service.CategorySubscriptionDomainService
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminMemberService(
    private val memberDomainService: MemberDomainService,
    private val companyDomainService: CompanyDomainService,
    private val categoryDomainService: CategoryDomainService,
    private val companySubscriptionDomainService: CompanySubscriptionDomainService,
    private val categorySubscriptionDomainService: CategorySubscriptionDomainService,
) {

    @Transactional
    fun updateMemberStatus(memberId: Long, request: PatchMemberStatusRequest) {
        val member = memberDomainService.findById(memberId)
            ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

        member.changeMemberStatus(request.memberStatus)
    }

    @Transactional(readOnly = true)
    fun getMemberDetail(memberId: Long): MemberDetailResponse {
        val member = memberDomainService.findById(memberId)
            ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

        val subscribedCompanies = companySubscriptionDomainService.findAllByMemberFetchCompany(member).map {
            MemberSubscribedCompanyResponse(
                companyId = it.company.id!!,
                companyNameKr = it.company.nameKr,
                companyNameEn = it.company.nameEn,
                logo = it.company.logo
            )
        }
        val subscribedCategories = categorySubscriptionDomainService.findAllByMemberFetchCategory(member).map {
            MemberSubscribedCategoryResponse(
                categoryId = it.category.id!!,
                categoryName = it.category.name
            )
        }

        return MemberDetailResponse(
            memberId = member.id!!,
            email = member.email,
            nickname = member.nickname,
            memberStatus = member.memberStatus,
            subscribedCompanies = subscribedCompanies,
            subscribedCategories = subscribedCategories
        )
    }

    @Transactional
    fun updateMember(memberId: Long, request: PutMemberRequest) {
        val member = memberDomainService.findById(memberId)
            ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

        member.email = request.email
        member.nickname = request.nickname
        memberDomainService.save(member)

        val companyIds = request.companyIds.distinct()
        val companies = companyDomainService.findAllOrByIds(companyIds)
        if (companies.size != companyIds.size) {
            throw ApplicationException(COMPANY_NOT_FOUND_ERROR)
        }

        val categoryIds = request.categoryIds.distinct()
        val categories = categoryDomainService.findAllOrByIds(categoryIds)
        if (categories.size != categoryIds.size) {
            throw ApplicationException(CATEGORY_NOT_FOUND_ERROR)
        }

        companySubscriptionDomainService.deleteAllByMemberId(memberId)
        categorySubscriptionDomainService.deleteAllByMemberId(memberId)

        companySubscriptionDomainService.saveAll(
            companies.map { company ->
                CompanySubscription(member = member, company = company)
            }
        )
        categorySubscriptionDomainService.saveAll(
            categories.map { category ->
                CategorySubscription(member = member, category = category)
            }
        )
    }

    @Transactional(readOnly = true)
    fun searchMembers(
        pageable: Pageable,
        statusFilter: MemberStatusFilter,
        searchType: MemberListSearchType?,
        keyword: String?,
        sortBy: MemberListSortBy,
        sortDirection: Sort.Direction,
    ): MemberListPageResponse {
        val trimmedKeyword = keyword?.trim()?.takeIf { it.isNotBlank() }
        val memberStatus = statusFilter.toMemberStatus()

        val (nicknameKeyword, emailKeyword) = when {
            trimmedKeyword == null -> null to null
            searchType == MemberListSearchType.NICKNAME -> trimmedKeyword to null
            searchType == MemberListSearchType.EMAIL -> null to trimmedKeyword
            else -> trimmedKeyword to trimmedKeyword
        }

        val memberPage = memberDomainService.findWithCondition(
            pageable = PageRequest.of(pageable.pageNumber, pageable.pageSize),
            memberStatus = memberStatus,
            nicknameKeyword = nicknameKeyword,
            emailKeyword = emailKeyword,
            sortBy = sortBy.toMemberSortBy(),
            sortDirection = sortDirection
        )

        val content = memberPage.content.map {
            MemberSimpleResponse(
                memberId = it.id!!,
                email = it.email,
                nickname = it.nickname,
                memberStatus = it.memberStatus,
                createdAt = it.createdAt!!
            )
        }

        val memberStatusCountResponse = MemberStatusCountResponse(
            allCount = memberDomainService.countByMemberStatus(null),
            activeCount = memberDomainService.countByMemberStatus(MemberStatus.ACTIVE),
            deletedCount = memberDomainService.countByMemberStatus(MemberStatus.DELETED)
        )

        return MemberListPageResponse(
            pageNumber = memberPage.number,
            totalCount = memberPage.totalElements,
            hasNext = memberPage.hasNext(),
            statusCount = memberStatusCountResponse,
            content = content
        )
    }

    private fun MemberStatusFilter.toMemberStatus(): MemberStatus? {
        return when (this) {
            MemberStatusFilter.ALL -> null
            MemberStatusFilter.ACTIVE -> MemberStatus.ACTIVE
            MemberStatusFilter.DELETED -> MemberStatus.DELETED
        }
    }

    private fun MemberListSortBy.toMemberSortBy(): MemberSortBy {
        return when (this) {
            MemberListSortBy.SUBSCRIBE_DATE -> MemberSortBy.CREATED_AT
            MemberListSortBy.NICKNAME -> MemberSortBy.NICKNAME
            MemberListSortBy.EMAIL -> MemberSortBy.EMAIL
            MemberListSortBy.STATUS -> MemberSortBy.STATUS
        }
    }
}
