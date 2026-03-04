package com.artijjaek.core.domain.mail.enums

enum class EmailOutboxStatus {
    PENDING,
    ENQUEUED,
    SENDING,
    SENT,
    FAIL,
    DEAD,
}
