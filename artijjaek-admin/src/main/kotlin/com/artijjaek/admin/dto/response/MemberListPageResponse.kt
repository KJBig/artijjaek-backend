package com.artijjaek.admin.dto.response

import com.artijjaek.admin.dto.common.PageResponse

data class MemberListPageResponse(
    override val pageNumber: Int,
    override val totalCount: Long,
    override val hasNext: Boolean,
    override val content: List<MemberSimpleResponse>,
    val statusCount: MemberStatusCountResponse,
) : PageResponse<MemberSimpleResponse>(
    pageNumber = pageNumber,
    totalCount = totalCount,
    hasNext = hasNext,
    content = content
)
