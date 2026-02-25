package com.artijjaek.admin.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class PostNoticeMailRequest(
    @field:NotEmpty
    val memberIds: List<Long>,

    @field:NotBlank
    val title: String,

    @field:NotBlank
    val content: String,
)
