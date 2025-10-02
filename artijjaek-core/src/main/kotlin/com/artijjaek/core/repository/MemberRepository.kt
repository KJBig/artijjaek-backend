package com.artijjaek.core.repository

import com.artijjaek.core.domain.Member
import com.artijjaek.core.enums.MemberStatus
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String): Member?
    fun findByEmailAndMemberStatus(email: String, active: MemberStatus): Member?
}