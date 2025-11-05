package com.artijjaek.core.domain.unsubscription.service

import com.artijjaek.core.domain.unsubscription.entity.Unsubscription
import com.artijjaek.core.domain.unsubscription.repository.UnsubscriptionRepository
import org.springframework.stereotype.Service

@Service
class UnsubscriptionDomainService(
    private val unsubscriptionRepository: UnsubscriptionRepository,
) {

    fun saveUnsubscription(unsubscription: Unsubscription): Unsubscription {
        return unsubscriptionRepository.save(unsubscription)
    }

}