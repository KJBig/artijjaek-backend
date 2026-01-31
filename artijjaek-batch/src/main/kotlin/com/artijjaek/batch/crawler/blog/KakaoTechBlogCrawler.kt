package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.RssCrawler
import org.springframework.stereotype.Component

@Component
class KakaoTechBlogCrawler(
) : RssCrawler() {

    override val blogName: String = "KAKAO TECH"

}