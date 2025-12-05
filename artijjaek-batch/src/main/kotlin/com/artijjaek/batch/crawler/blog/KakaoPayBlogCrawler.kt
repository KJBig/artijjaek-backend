package com.artijjaek.batch.crawler.blog

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

@Component
class KakaoPayBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "KAKAO PAY TECH"

    override fun getItems(doc: Document): List<Element> {
        val items = doc.select("item").reversed().take(10)
        return items
    }

}