package com.artijjaek.core.domain.member.service

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberSortBy
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class MemberDomainService(
    private val memberRepository: MemberRepository,
) {

    fun save(member: Member): Member {
        return memberRepository.save(member)
    }

    fun findByEmail(email: String): Member? {
        return memberRepository.findByEmail(email)
    }

    fun findByEmailAndMemberStatus(email: String, memberStatus: MemberStatus): Member? {
        return memberRepository.findByEmailAndMemberStatus(email, memberStatus)
    }

    fun findById(memberId: Long): Member? {
        return memberRepository.findById(memberId).orElse(null)
    }

    fun findWithCondition(
        pageable: Pageable,
        memberStatus: MemberStatus?,
        nicknameKeyword: String?,
        emailKeyword: String?,
        sortBy: MemberSortBy,
        sortDirection: Sort.Direction,
    ): Page<Member> {
        return memberRepository.findWithCondition(
            pageable = pageable,
            memberStatus = memberStatus,
            nicknameKeyword = nicknameKeyword,
            emailKeyword = emailKeyword,
            sortBy = sortBy,
            sortDirection = sortDirection
        )
    }

    fun countByMemberStatus(memberStatus: MemberStatus?): Long {
        return memberRepository.countByMemberStatus(memberStatus)
    }

}
