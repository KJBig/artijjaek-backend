package com.artijjaek.core.domain.mail.dto

import java.time.LocalDateTime

data class ProcessResult(
    val skipped: Boolean,
    val nextRetryAt: LocalDateTime?,
)