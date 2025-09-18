package com.artiting.core.service

import com.artiting.core.domain.Member
import com.artiting.core.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberDomainService(
    private val memberRepository: MemberRepository,
) {

    fun save(member: Member): Member {
        return memberRepository.save(member)
    }

}