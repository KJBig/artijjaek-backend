package com.artijjaek.admin.service

import com.artijjaek.admin.dto.response.MemberListPageResponse
import com.artijjaek.admin.dto.response.MemberSimpleResponse
import com.artijjaek.admin.dto.response.MemberStatusCountResponse
import com.artijjaek.admin.enums.MemberListSearchType
import com.artijjaek.admin.enums.MemberListSortBy
import com.artijjaek.admin.enums.MemberStatusFilter
import com.artijjaek.core.domain.member.enums.MemberSortBy
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberDomainService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminMemberService(
    private val memberDomainService: MemberDomainService,
) {

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
