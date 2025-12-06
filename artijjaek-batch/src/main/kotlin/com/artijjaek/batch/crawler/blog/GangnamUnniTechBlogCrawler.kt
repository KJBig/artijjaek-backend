package com.artijjaek.batch.crawler.blog

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

@Component
class GangnamUnniTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "GANGNAM UNNI TECH"

    override fun getItems(doc: Document): List<Element> {
        return doc.select("item").reversed().take(10)
    }

}