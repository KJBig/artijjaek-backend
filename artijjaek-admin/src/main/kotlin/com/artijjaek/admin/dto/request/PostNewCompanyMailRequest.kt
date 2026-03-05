package com.artijjaek.admin.dto.request

import jakarta.validation.constraints.NotEmpty

data class PostNewCompanyMailRequest(
    @field:NotEmpty
    val memberIds: List<Long>,

    @field:NotEmpty
    val companyIds: List<Long>,
)
