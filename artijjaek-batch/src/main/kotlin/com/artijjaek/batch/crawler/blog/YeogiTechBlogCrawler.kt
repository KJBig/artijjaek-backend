package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class YeogiTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "YEOGI TECH"

}