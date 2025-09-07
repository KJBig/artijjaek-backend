package com.noati.core.service

import com.noati.core.domain.Member
import com.noati.core.domain.Subscribe
import com.noati.core.repository.SubscribeRepository
import org.springframework.stereotype.Service

@Service
class SubscribeDomainService(
    val subscribeRepository: SubscribeRepository,
) {
    fun findAllByMember(member: Member): List<Subscribe> {
        return subscribeRepository.findAllByMember(member)
    }
}