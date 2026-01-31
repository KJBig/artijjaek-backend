package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

@Component
class KakaoPayBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "KAKAO PAY TECH"

    override fun getItems(doc: Document): List<Element> {
        return doc.select("item").takeLast(10)
    }

}