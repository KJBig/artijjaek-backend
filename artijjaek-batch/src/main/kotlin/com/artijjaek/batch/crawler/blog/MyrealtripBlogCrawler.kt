package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class MyrealtripBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "MYREALTRIP"

}