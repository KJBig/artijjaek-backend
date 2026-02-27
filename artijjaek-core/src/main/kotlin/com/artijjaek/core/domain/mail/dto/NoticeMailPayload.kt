package com.artijjaek.core.domain.mail.dto

data class NoticeMailPayload(
    val member: MemberSnapshot,
    val title: String,
    val content: String,
)
