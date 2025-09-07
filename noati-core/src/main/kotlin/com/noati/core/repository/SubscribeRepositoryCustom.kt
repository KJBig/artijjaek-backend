package com.noati.core.repository

import com.noati.core.domain.Member
import com.noati.core.domain.Subscribe

interface SubscribeRepositoryCustom {
    fun findAllByMember(member: Member): List<Subscribe>
}