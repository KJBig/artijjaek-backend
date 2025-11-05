package com.artijjaek.api.dto.request

import com.artijjaek.core.domain.unsubscription.enums.UnSubscriptionReason

data class UnsubscriptionRequest(
    val email: String,
    val token: String,
    val reason: UnSubscriptionReason,
    val detail: String,
)