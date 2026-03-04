package com.artijjaek.core.domain.mail.repository

import com.artijjaek.core.domain.mail.entity.EmailOutbox
import org.springframework.data.jpa.repository.JpaRepository

interface EmailOutboxRepository : JpaRepository<EmailOutbox, Long>, EmailOutboxRepositoryCustom
