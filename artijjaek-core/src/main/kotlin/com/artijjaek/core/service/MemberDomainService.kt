package com.artijjaek.core.service

import com.artijjaek.core.domain.Member
import com.artijjaek.core.enums.MemberStatus
import com.artijjaek.core.repository.MemberRepository
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