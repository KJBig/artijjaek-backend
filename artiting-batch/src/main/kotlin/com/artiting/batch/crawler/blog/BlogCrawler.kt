package com.artiting.batch.crawler.blog

import com.artiting.core.domain.Article
import com.artiting.core.domain.Company

interface BlogCrawler {
    val getBlogName: String
    fun crawl(company: Company): List<Article>
}