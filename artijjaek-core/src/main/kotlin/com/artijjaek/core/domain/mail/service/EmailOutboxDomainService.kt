package com.artijjaek.core.domain.mail.service

import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.repository.EmailOutboxRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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

    fun findById(id: Long): EmailOutbox? {
        return emailOutboxRepository.findById(id).orElse(null)
    }

    fun findDueIds(now: LocalDateTime, limit: Int): List<Long> {
        return emailOutboxRepository.findDueIds(now, limit)
    }

    fun existsDue(now: LocalDateTime): Boolean {
        return emailOutboxRepository.existsDue(now)
    }

    fun claimForSending(id: Long, now: LocalDateTime): Boolean {
        return emailOutboxRepository.claimForSending(id, now)
    }

    fun findEarliestRetryAt(): LocalDateTime? {
        return emailOutboxRepository.findEarliestRetryAt()
    }
}
