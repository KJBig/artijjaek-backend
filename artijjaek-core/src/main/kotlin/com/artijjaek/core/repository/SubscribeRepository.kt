package com.artijjaek.core.repository

import com.artijjaek.core.domain.Subscribe
import org.springframework.data.jpa.repository.JpaRepository

interface SubscribeRepository : JpaRepository<Subscribe, Long>, SubscribeRepositoryCustom {
    fun deleteAllByMemberId(memberId: Long?)
}