package com.artijjaek.admin.dto.request

import com.artijjaek.core.domain.company.enums.CrawlOrder
import com.artijjaek.core.domain.company.enums.CrawlPattern

data class PostCompanyRequest(
    val nameKr: String,
    val nameEn: String,
    val logo: String,
    val baseUrl: String,
    val blogUrl: String,
    val crawlUrl: String,
    val crawlAvailability: Boolean,
    val crawlPattern: CrawlPattern,
    val crawlOrder: CrawlOrder,
)
