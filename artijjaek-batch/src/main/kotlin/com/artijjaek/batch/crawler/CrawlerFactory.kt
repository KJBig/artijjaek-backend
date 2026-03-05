package com.artijjaek.batch.crawler

import com.artijjaek.batch.crawler.patter.PatternCrawler
import com.artijjaek.batch.crawler.specific.CompanySpecificCrawler
import com.artijjaek.core.domain.company.entity.Company
import org.springframework.stereotype.Component

@Component
class CrawlerFactory(
    patternCrawlers: List<PatternCrawler>,
    companySpecificCrawlers: List<CompanySpecificCrawler>,
) {

    private val patternCrawlerMap: Map<String, PatternCrawler> =
        patternCrawlers.associateBy { it.pattern.name }
    private val companyCrawlerMap: Map<String, CompanySpecificCrawler> =
        companySpecificCrawlers.associateBy { it.companyNameEn.uppercase() }

    fun getCrawler(company: Company): Crawler {
        companyCrawlerMap[company.nameEn.uppercase()]?.let { return it }

        return patternCrawlerMap[company.crawlPattern.name]
            ?: throw IllegalArgumentException(
                "Crawler not found for company: ${company.nameEn}, pattern: ${company.crawlPattern}"
            )
    }
}
