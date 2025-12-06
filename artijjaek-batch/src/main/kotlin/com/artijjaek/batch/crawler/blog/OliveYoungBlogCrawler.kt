package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class OliveYoungBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "OLIVE YOUNG"

}