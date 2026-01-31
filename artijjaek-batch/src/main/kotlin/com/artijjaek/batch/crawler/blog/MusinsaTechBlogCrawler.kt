package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import org.springframework.stereotype.Component

@Component
class MusinsaTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "MUSINSA TECH"

}