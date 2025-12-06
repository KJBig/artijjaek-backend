package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class NhnCloudBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "NHN CLOUD"

}