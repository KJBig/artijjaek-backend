package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import com.artijjaek.core.domain.company.entity.Company
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

@Component
class AwsTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "AWS TECH"

    override fun findFirstTextElement(element: Element): String? {
        return null
    }

    override fun findFirstImageElement(htmlDoc: Document, company: Company): String? {
        return null
    }

}