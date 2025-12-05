package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.dto.CrawledArticleDto
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

abstract class RssCrawler(
) : BlogCrawler {

    private val log = LoggerFactory.getLogger(RssCrawler::class.java)

    abstract override val blogName: String

    override fun crawl(company: Company): List<Article> {
        log.info("==== Crawling RSS feed for company: [${company.nameKr}] ====")
        val url = URL(company.baseUrl + company.crawlUrl)
        val xml = url.readText()

        val doc = Jsoup.parse(xml, "", Parser.xmlParser())
        val items = getItems(doc)

        return items.map { item ->
            val crawledArticleData = mapItemToDto(item, company)

            log.info(
                "[${company.nameKr}] : " +
                        "Title->${crawledArticleData.title}, " +
                        "Link->${crawledArticleData.link}, " +
                        "Img->${crawledArticleData.firstImg}, " +
                        "Description->${crawledArticleData.firstText}"
            )

            Article(
                company = company,
                title = crawledArticleData.title,
                description = crawledArticleData.firstText,
                link = crawledArticleData.link,
                image = crawledArticleData.firstImg,
                category = null
            )
        }

    }

    protected open fun getItems(doc: Document): List<Element> {
        val items = doc.select("item").take(10)
        return items
    }

    protected open fun mapItemToDto(
        item: Element,
        company: Company
    ): CrawledArticleDto {
        val title = item.selectFirst("title")?.text()
        val link = item.selectFirst("link")?.text()

        val contentHtml = item.selectFirst("content|encoded")?.wholeText()

        val htmlDoc = Jsoup.parse(contentHtml ?: "")
        val firstText = findFirstTextElement(htmlDoc.body())
        val firstImg = findFirstImageElement(htmlDoc, company)

        return CrawledArticleDto(
            title = title ?: "",
            link = link ?: "",
            firstText = firstText ?: "",
            firstImg = firstImg ?: ""
        )
    }

    protected open fun findFirstTextElement(element: Element): String? {
        val queue: Queue<Element> = LinkedList(element.children())
        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val text = current.text().trim()
            if (text.isNotEmpty()) {
                return text
            }
            queue.addAll(current.children())
        }
        return null
    }


    protected open fun findFirstImageElement(htmlDoc: Document, company: Company): String? {
        val rawImg = htmlDoc.select("img").firstOrNull()?.attr("src")
        return rawImg?.let { img ->
            if (img.startsWith("http")) img
            else company.baseUrl + img
        }
    }
}