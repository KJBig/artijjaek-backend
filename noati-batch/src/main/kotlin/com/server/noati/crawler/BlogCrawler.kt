package com.server.noati.crawler

import com.server.noati.domain.Article
import com.server.noati.domain.Company

interface BlogCrawler {
    val getBlogName: String
    fun crawl(company: Company): List<Article>
}