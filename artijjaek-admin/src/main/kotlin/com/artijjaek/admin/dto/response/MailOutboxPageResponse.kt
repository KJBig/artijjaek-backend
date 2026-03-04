package com.artijjaek.admin.dto.response

import com.artijjaek.admin.dto.common.PageResponse

data class MailOutboxPageResponse(
    override val pageNumber: Int,
    override val totalCount: Long,
    override val hasNext: Boolean,
    override val content: List<MailOutboxSimpleResponse>,
) : PageResponse<MailOutboxSimpleResponse>(
    pageNumber = pageNumber,
    totalCount = totalCount,
    hasNext = hasNext,
    content = content
)
