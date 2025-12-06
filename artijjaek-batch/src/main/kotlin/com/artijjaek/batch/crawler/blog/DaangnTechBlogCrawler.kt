package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class DaangnTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "DAANGN TECH"

}