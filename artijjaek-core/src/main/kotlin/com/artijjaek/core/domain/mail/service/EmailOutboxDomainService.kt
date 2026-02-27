package com.artijjaek.core.domain.mail.service

import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.repository.EmailOutboxRepository
import org.springframework.stereotype.Service

@Service
class EmailOutboxDomainService(
    private val emailOutboxRepository: EmailOutboxRepository,
) {
    fun save(emailOutbox: EmailOutbox): EmailOutbox {
        return emailOutboxRepository.save(emailOutbox)
    }

    fun saveAll(emailOutboxes: List<EmailOutbox>): List<EmailOutbox> {
        return emailOutboxRepository.saveAll(emailOutboxes)
    }
}
