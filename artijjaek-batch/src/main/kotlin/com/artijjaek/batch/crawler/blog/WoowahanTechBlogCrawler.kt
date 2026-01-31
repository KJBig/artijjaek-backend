package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import com.artijjaek.core.domain.company.entity.Company
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component

@Component
class WoowahanTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "WOOWAHAN TECH"

    override fun findFirstImageElement(htmlDoc: Document, company: Company): String? {
        return null
    }

}