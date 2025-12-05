package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.dto.CrawledArticleDto
import com.artijjaek.core.domain.company.entity.Company
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

@Component
class NaverD2BlogCrawler(
) : RssCrawler() {

    override val blogName: String = "NAVER D2"

    override fun getItems(doc: Document): List<Element> {
        return doc.select("entry").take(10)
    }

    override fun mapItemToDto(
        item: Element,
        company: Company
    ): CrawledArticleDto {
        val title = item.selectFirst("title")?.text()
        val link = item.selectFirst("link")?.attr("href")
        val contentHtml = item.selectFirst("content")?.wholeText()

        val htmlDoc = Jsoup.parse(contentHtml ?: "")
        val firstText = findFirstTextElement(htmlDoc.body())
        val firstImg = findFirstImageElement(htmlDoc, company)

        return CrawledArticleDto(
            title = title ?: "",
            link = link ?: "",
            firstText = firstText,
            firstImg = firstImg
        )
    }

    override fun findFirstTextElement(element: Element): String? {
        return null
    }

    override fun findFirstImageElement(htmlDoc: Document, company: Company): String? {
        return null
    }
}