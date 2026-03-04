package com.artijjaek.core.domain.mail.repository

import com.artijjaek.core.domain.mail.entity.EmailOutboxAttempt
import org.springframework.data.jpa.repository.JpaRepository

interface EmailOutboxAttemptRepository : JpaRepository<EmailOutboxAttempt, Long>, EmailOutboxAttemptRepositoryCustom
