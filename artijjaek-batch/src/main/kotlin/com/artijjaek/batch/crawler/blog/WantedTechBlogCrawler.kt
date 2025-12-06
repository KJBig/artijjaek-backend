package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class WantedTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "WANTED TECH"

}