package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class DaangnTechBlogOldCrawler(
) : RssCrawler() {

    override val blogName: String = "DAANGN TECH"

}