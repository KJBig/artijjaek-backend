package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class KurlyBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "KURLY"

}