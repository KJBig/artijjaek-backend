package com.artijjaek.batch.crawler.patter

import com.artijjaek.batch.crawler.Crawler
import com.artijjaek.core.domain.company.enums.CrawlPattern

interface PatternCrawler : Crawler {
    val pattern: CrawlPattern
}
