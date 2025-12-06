package com.artijjaek.batch.crawler.blog

import org.springframework.stereotype.Component

@Component
class YogiyoTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "YOGIYO TECH"

}