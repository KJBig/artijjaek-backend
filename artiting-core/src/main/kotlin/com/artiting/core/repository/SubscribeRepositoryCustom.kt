package com.artiting.core.repository

import com.artiting.core.domain.Member
import com.artiting.core.domain.Subscribe

interface SubscribeRepositoryCustom {
    fun findAllByMember(member: Member): List<Subscribe>
}