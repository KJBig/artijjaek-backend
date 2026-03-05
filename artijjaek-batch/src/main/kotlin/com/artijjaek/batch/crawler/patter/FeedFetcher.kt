package com.artijjaek.batch.crawler.patter

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

object FeedFetcher {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    fun fetch(feedUrl: String): String {
        val request = Request.Builder()
            .url(feedUrl)
            .header("User-Agent", BROWSER_USER_AGENT)
            .header("Accept", "application/rss+xml, application/atom+xml, application/xml;q=0.9, text/xml;q=0.8, */*;q=0.7")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .header("Referer", "https://www.google.com/")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Feed fetch failed (${response.code}): $feedUrl")
            }
            return response.body?.string()
                ?: throw IOException("Feed response body is empty: $feedUrl")
        }
    }

    private const val BROWSER_USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
}
