package com.artijjaek.batch.crawler.patter

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.enums.CrawlPattern
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RssPatternCrawler : PatternCrawler {

    private val log = LoggerFactory.getLogger(RssPatternCrawler::class.java)

    override val pattern: CrawlPattern = CrawlPattern.RSS

    override fun crawl(company: Company): List<Article> {
        log.info("==== Crawling RSS feed for company: [${company.nameKr}] ====")

        val feedUrl = company.baseUrl + company.crawlUrl
        val xml = FeedFetcher.fetch(feedUrl)
        val doc = Jsoup.parse(xml, "", Parser.xmlParser())

        return doc.select("item")
            .take(10)
            .map { item ->
                val title = item.selectFirst("title")?.text().orEmpty()
                val link = item.selectFirst("link")?.text().orEmpty()

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
