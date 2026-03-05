package com.artijjaek.admin.dto.response

import com.artijjaek.core.domain.company.enums.CrawlOrder
import com.artijjaek.core.domain.company.enums.CrawlPattern
import java.time.LocalDateTime

data class CompanySimpleResponse(
    val companyId: Long,
    val nameKr: String,
    val nameEn: String,
    val logo: String,
    val baseUrl: String,
    val blogUrl: String,
    val crawlUrl: String,
    val crawlAvailability: Boolean,
    val crawlPattern: CrawlPattern,
    val crawlOrder: CrawlOrder,
    val createdAt: LocalDateTime,
)
