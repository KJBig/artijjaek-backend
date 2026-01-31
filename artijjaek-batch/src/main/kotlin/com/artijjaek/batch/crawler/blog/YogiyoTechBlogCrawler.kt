package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import org.springframework.stereotype.Component

@Component
class YogiyoTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "YOGIYO TECH"

}