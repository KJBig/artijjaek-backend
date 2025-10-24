package com.artijjaek.batch.crawler.blog

import com.artijjaek.batch.crawler.UrlDataCrawler
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.company.entity.Company
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DaangnTechBlogCrawler(
    private val urlDataCrawler: UrlDataCrawler,
) : BlogCrawler {

    private val log = LoggerFactory.getLogger(DaangnTechBlogCrawler::class.java)

    override val getBlogName: String
        get() = "DAANGN TECH"

    override fun crawl(company: Company): List<Article> {
        val url: String = company.baseUrl + company.crawlUrl
        val articles = mutableListOf<Article>()

        try {
            val doc: Document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .referrer("https://medium.com/")
                .timeout(10000)
                .get()

            val articleElements = findArticleElements(doc)

            if (articleElements.isEmpty()) {
                log.warn("글 목록을 찾을 수 없습니다. URL: $url")
                return articles
            }

            articleElements.stream()
                .forEach {
                    try {
                        val articleUrl = findArticleUrl(it, url)
                        val crawlingUrlData = urlDataCrawler.crawlingUrlData(articleUrl)
                        articles.add(
                            Article(
                                company = company,
                                title = crawlingUrlData.title,
                                description = crawlingUrlData.description,
                                link = articleUrl,
                                image = crawlingUrlData.imageUrl,
                                category = null
                            )
                        )
                    } catch (e: Exception) {
                        log.error("아티클 처리 실패: ${e.message}", e)
                    }
                }

            return articles.distinctBy { it.link }

        } catch (e: Exception) {
            log.error("크롤링 실패: ${e.message}", e)
        }

        return articles
    }

    private fun findArticleElements(doc: Document): List<Element> {
        // 1) div[data-href] 요소 찾기 (새로운 구조)
        var elements = doc.select("div[data-href]")
        if (elements.isNotEmpty()) {
            log.debug("div[data-href]로 발견: ${elements.size}개")
            return elements.toList()
        }

        // 2) 기존의 a[data-href]
        elements = doc.select("a[data-href]")
        if (elements.isNotEmpty()) {
            log.debug("a[data-href]로 발견: ${elements.size}개")
            return elements.toList()
        }

        // 3) 모든 data-href 속성을 가진 요소 (포괄적)
        elements = doc.select("[data-href]")
        if (elements.isNotEmpty()) {
            log.debug("[data-href]로 발견: ${elements.size}개")
            return elements.toList()
        }

        // 4) article 태그 내의 data-href 링크
        elements = doc.select("article [data-href]")
        if (elements.isNotEmpty()) {
            log.debug("article [data-href]로 발견: ${elements.size}개")
            return elements.toList()
        }

        // 5) 제목 태그를 가진 컨테이너 내 data-href 링크
        elements = doc.select("div[data-href][role=link]")
        if (elements.isNotEmpty()) {
            log.debug("제목 포함 div [data-href]로 발견: ${elements.size}개")
            return elements.toList()
        }

        return emptyList()
    }

    private fun findArticleUrl(element: Element, baseUrl: String): String {
        try {
            val href = element.attr("abs:data-href").ifBlank { element.attr("data-href") }

            return when {
                href.startsWith("http") -> href
                href.startsWith("/") -> baseUrl.removeSuffix("/") + href
                else -> "$baseUrl/${href.removePrefix("/")}"
            }
        } catch (e: Exception) {
            log.error("url 찾기 실패 with Base Url : $baseUrl")
            throw e
        }
    }
}