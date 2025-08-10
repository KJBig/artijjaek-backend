package com.noati.batch.crawler

import com.noati.core.domain.Article
import com.noati.core.domain.Company

interface BlogCrawler {
    val getBlogName: String
    fun crawl(company: Company): List<Article>
}