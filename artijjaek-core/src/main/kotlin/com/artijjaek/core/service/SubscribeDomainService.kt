package com.artijjaek.core.service

import com.artijjaek.core.domain.Member
import com.artijjaek.core.domain.Subscribe
import com.artijjaek.core.repository.SubscribeRepository
import org.springframework.stereotype.Service

@Service
class SubscribeDomainService(
    val subscribeRepository: SubscribeRepository,
) {
    fun findAllByMember(member: Member): List<Subscribe> {
        return subscribeRepository.findAllByMember(member)
    }

    fun saveAll(subscribes: List<Subscribe>) {
        subscribeRepository.saveAll(subscribes)
    }

    fun deleteAllByMemberId(memberId: Long) {
        subscribeRepository.deleteAllByMemberId(memberId)
    }
}