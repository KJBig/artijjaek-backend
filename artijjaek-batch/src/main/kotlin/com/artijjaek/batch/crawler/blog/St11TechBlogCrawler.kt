package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class St11TechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "11ST TECH"

}