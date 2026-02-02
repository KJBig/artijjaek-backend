package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import org.springframework.stereotype.Component

@Component
class WantedTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "WANTED TECH"

}