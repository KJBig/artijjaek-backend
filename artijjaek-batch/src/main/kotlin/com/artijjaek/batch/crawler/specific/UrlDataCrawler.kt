package com.artijjaek.batch.crawler.specific

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UrlDataCrawler {

    private val log = LoggerFactory.getLogger(UrlDataCrawler::class.java)

    fun crawlingUrlData(url: String): String {
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

private fun extractData(head: Element): String {
    val ogTitle = head.select("meta[property=og:title]").attr("content")
    return ogTitle
}