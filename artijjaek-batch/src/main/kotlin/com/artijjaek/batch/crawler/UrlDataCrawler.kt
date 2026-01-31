package com.artijjaek.batch.crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

@Component
class UrlDataCrawler {

    private val log = org.slf4j.LoggerFactory.getLogger(UrlDataCrawler::class.java)

    fun crawlingUrlData(url: String): UrlData {
        try {
            val doc: Document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            // head 가져오기
            val head = doc.head()

            // 메타 데이터 추출
            return extractData(head)
        } catch (e: Exception) {
            log.error("Failed to extract data from url [${url}], ${e.message}")
            throw e
        }
    }
}

private fun extractData(head: Element): UrlData {
    val ogTitle = head.select("meta[property=og:title]").attr("content")
    val ogImage = head.select("meta[property=og:image]").attr("content")

    return UrlData(ogTitle, ogImage)
}