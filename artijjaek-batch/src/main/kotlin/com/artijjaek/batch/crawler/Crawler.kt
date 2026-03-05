package com.artijjaek.batch.crawler

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company

interface Crawler {
    fun crawl(company: Company): List<Article>
}
