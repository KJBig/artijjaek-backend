package com.artijjaek.admin.dto.response

import com.artijjaek.admin.dto.common.PageResponse

data class CompanyListPageResponse(
    override val pageNumber: Int,
    override val totalCount: Long,
    override val hasNext: Boolean,
    override val content: List<CompanySimpleResponse>,
) : PageResponse<CompanySimpleResponse>(
    pageNumber = pageNumber,
    totalCount = totalCount,
    hasNext = hasNext,
    content = content
)
