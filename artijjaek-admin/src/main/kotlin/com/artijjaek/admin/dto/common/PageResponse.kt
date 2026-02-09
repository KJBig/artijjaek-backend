package com.artijjaek.admin.dto.common

open class PageResponse<T>(
    open val pageNumber: Int,
    open val totalCount: Long,
    open val hasNext: Boolean,
    open val content: List<T>,
)
