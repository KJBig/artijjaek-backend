package com.artiting.core.repository

import com.artiting.core.domain.Member
import com.artiting.core.enums.MemberStatus
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String): Member?
    fun findByEmailAndMemberStatus(email: String, active: MemberStatus): Member?
}