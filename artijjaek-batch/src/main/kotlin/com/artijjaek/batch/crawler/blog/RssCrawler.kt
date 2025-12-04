package com.artijjaek.batch.crawler.blog

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.net.URL

abstract class RssCrawler(
) : BlogCrawler {

    private val log = LoggerFactory.getLogger(OliveYoungBlogCrawler::class.java)

    abstract override val blogName: String

    override fun crawl(company: Company): List<Article> {
        log.info("==== Crawling RSS feed for company: [${company.nameKr}] ====")
        val url = URL(company.baseUrl + company.crawlUrl)
        val xml = url.readText()

        val doc = Jsoup.parse(xml, "", Parser.xmlParser())
        val items = doc.select("item").take(10)

        return items.map { item ->
            val title = item.selectFirst("title")?.text()
            val link = item.selectFirst("link")?.text()

            val contentHtml = item.selectFirst("content|encoded")?.wholeText()

            val htmlDoc = Jsoup.parse(contentHtml ?: "")
            val firstText = findFirstTextElement(htmlDoc.body())
            val firstImg = htmlDoc.select("img").firstOrNull()?.attr("src")

            log.info("[${company.nameKr}] : Title->$title, Link->$link, Img->$firstImg, Description->$firstText")

            Article(
                company = company,
                title = title ?: "",
                description = firstText,
                link = link ?: "",
                image = firstImg,
                category = null
            )

        }

    }

    fun findFirstTextElement(element: Element): String? {
        for (child in element.children()) {
            val text = child.text().trim()
            if (text.isNotEmpty()) {
                return text
            }

            val nested = findFirstTextElement(child)
            if (nested != null) {
                return nested
            }
        }
        return null
    }
}