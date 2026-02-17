package com.artijjaek.api.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class SubscriptionChangeRequest(
    @field:Email
    @field:Size(max = 255, message = "이메일이 최대 길이를 넘었습니다.")
    val email: String,

    @field:NotBlank(message = "사용자 토큰이 비어있습니다.")
    val token: String,

    @field:NotBlank(message = "닉네임이 비어있습니다.")
    @field:Size(max = 255, message = "닉네임이 최대 길이를 넘었습니다.")
    val nickname: String,

    @field:NotEmpty(message = "카테고리를 선택하세요.")
    val categoryIds: List<Long>,

    @field:NotEmpty(message = "회사를 선택하세요.")
    val companyIds: List<Long>,
)
