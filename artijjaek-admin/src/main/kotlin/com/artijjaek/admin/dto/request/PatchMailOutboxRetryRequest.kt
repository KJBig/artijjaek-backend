package com.artijjaek.admin.dto.request

data class PatchMailOutboxRetryRequest(
    val resetAttempts: Boolean = false,
)
