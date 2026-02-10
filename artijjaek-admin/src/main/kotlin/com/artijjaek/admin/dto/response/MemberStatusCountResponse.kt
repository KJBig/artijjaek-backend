package com.artijjaek.admin.dto.response

data class MemberStatusCountResponse(
    val allCount: Long,
    val activeCount: Long,
    val deletedCount: Long,
)
