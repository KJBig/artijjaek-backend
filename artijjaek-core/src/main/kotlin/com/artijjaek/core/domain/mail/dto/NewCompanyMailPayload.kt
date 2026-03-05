package com.artijjaek.core.domain.mail.dto

data class NewCompanyMailPayload(
    val member: MemberSnapshot,
    val companies: List<CompanySnapshot>,
)
