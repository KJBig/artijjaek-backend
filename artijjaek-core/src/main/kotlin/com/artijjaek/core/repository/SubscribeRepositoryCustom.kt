package com.artijjaek.core.repository

import com.artijjaek.core.domain.Member
import com.artijjaek.core.domain.Subscribe

interface SubscribeRepositoryCustom {
    fun findAllByMember(member: Member): List<Subscribe>
}