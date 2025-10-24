package com.artijjaek.batch.crawler.blog

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company

interface BlogCrawler {
    val getBlogName: String
    fun crawl(company: Company): List<Article>
}