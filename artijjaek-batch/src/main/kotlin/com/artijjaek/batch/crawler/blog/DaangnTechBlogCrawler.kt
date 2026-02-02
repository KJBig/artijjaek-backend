package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import org.springframework.stereotype.Component

@Component
class DaangnTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "DAANGN TECH"

}