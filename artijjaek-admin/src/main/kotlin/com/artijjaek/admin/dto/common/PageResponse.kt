package com.artijjaek.admin.dto.common

data class PageResponse<T>(
    val pageNumber: Int,
    val hasNext: Boolean,
    val content: List<T>,
)