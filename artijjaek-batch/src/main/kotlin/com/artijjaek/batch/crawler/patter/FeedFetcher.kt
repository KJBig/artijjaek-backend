package com.artijjaek.batch.crawler.patter

import org.jsoup.Jsoup

object FeedFetcher {

    fun fetch(feedUrl: String): String {
        return Jsoup.connect(feedUrl)
            .userAgent(BROWSER_USER_AGENT)
            .referrer("https://www.google.com/")
            .header("Accept", "application/rss+xml, application/atom+xml, application/xml;q=0.9, text/xml;q=0.8, */*;q=0.7")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .ignoreContentType(true)
            .timeout(10000)
            .execute()
            .body()
    }

    private const val BROWSER_USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
}
