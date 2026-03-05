package com.artijjaek.batch.crawler.patter

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CrawlPattern
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RssEntryPatternCrawler : PatternCrawler {

    private val log = LoggerFactory.getLogger(RssEntryPatternCrawler::class.java)

    override val pattern: CrawlPattern = CrawlPattern.RSS_ENTRY

    override fun crawl(company: Company): List<Article> {
        log.info("==== Crawling RSS Entry feed for company: [${company.nameKr}] ====")

        val feedUrl = company.baseUrl + company.crawlUrl
        val xml = FeedFetcher.fetch(feedUrl)
        val doc = Jsoup.parse(xml, "", Parser.xmlParser())

        return doc.select("entry")
            .map { entry ->
                val title = entry.selectFirst("title")?.text().orEmpty()
                val linkElement = entry.selectFirst("link")
                val link = linkElement?.attr("href").takeUnless { it.isNullOrBlank() } ?: linkElement?.text().orEmpty()

                log.info("[${company.nameKr}] : Title->$title, Link->$link")

                Article(
                    company = company,
                    title = title,
                    link = link,
                    image = null,
                    description = null,
                    category = null
                )
            }
    }
}
