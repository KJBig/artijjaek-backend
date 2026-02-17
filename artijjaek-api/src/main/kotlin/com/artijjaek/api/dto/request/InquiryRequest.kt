package com.artijjaek.api.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class InquiryRequest(
    @field:Email
    @field:Size(max = 255, message = "이메일이 최대 길이를 넘었습니다.")
    val email: String,

    @field:NotEmpty(message = "내용을 입력하세요.")
    val content: String,
)
