package com.artijjaek.core.domain.mail.repository

import java.time.LocalDateTime

interface EmailOutboxRepositoryCustom {
    fun findDueIds(now: LocalDateTime, limit: Int): List<Long>
    fun existsDue(now: LocalDateTime): Boolean
    fun findEarliestRetryAt(): LocalDateTime?
    fun claimForSending(id: Long, now: LocalDateTime): Boolean
}
