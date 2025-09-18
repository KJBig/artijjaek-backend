package com.artiting.batch.crawler

import com.artiting.batch.crawler.blog.BlogCrawler
import org.springframework.stereotype.Component

@Component
class CrawlerFactory(crawlers: List<BlogCrawler>) {

    private val crawlerMap: Map<String, BlogCrawler> = crawlers.associateBy { it.getBlogName.uppercase() }

    fun getCrawler(companyName: String): BlogCrawler {
        return crawlerMap[companyName.uppercase()]
            ?: throw IllegalArgumentException("Crawler not found for company: $companyName")
    }
}
