package com.artijjaek.core.domain.member.repository

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>, MemberRepositoryCustom {
    fun findByEmail(email: String): Member?
    fun findByEmailAndMemberStatus(email: String, active: MemberStatus): Member?
}
