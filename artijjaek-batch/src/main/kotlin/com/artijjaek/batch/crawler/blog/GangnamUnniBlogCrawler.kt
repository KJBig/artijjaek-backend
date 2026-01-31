package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

@Component
class GangnamUnniBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "GANGNAM UNNI"

    override fun getItems(doc: Document): List<Element> {
        return doc.select("item").reversed().take(10)
    }

}