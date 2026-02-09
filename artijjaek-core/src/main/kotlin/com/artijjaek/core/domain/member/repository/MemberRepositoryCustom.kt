package com.artijjaek.core.domain.member.repository

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberSortBy
import com.artijjaek.core.domain.member.enums.MemberStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface MemberRepositoryCustom {
    fun findWithCondition(
        pageable: Pageable,
        memberStatus: MemberStatus?,
        nicknameKeyword: String?,
        emailKeyword: String?,
        sortBy: MemberSortBy,
        sortDirection: Sort.Direction,
    ): Page<Member>

    fun countByMemberStatus(memberStatus: MemberStatus?): Long
}
