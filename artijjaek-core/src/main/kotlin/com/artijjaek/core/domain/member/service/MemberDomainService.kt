package com.artijjaek.core.domain.member.service

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.repository.MemberRepository
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

}