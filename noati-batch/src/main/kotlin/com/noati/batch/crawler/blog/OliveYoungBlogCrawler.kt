package com.noati.batch.crawler.blog

import com.noati.batch.crawler.UrlDataCrawler
import com.noati.core.domain.Article
import com.noati.core.domain.Company
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OliveYoungBlogCrawler(
    val urlDataCrawler: UrlDataCrawler,
) : BlogCrawler {

    private val log = LoggerFactory.getLogger(OliveYoungBlogCrawler::class.java)

    override val getBlogName: String
        get() = "OLIVE YOUNG"

    override fun crawl(company: Company): List<Article> {
        val url: String = company.blogUrl
        val articles = mutableListOf<Article>()

        try {
            val doc: Document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get()

            val articleElements = findArticleElements(doc)

            if (articleElements.isEmpty()) {
                log.warn("글 목록을 찾을 수 없습니다. URL: $url")
                return articles
            }

            articleElements.stream()
                .forEach {
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
                }

            return articles

        } catch (e: Exception) {
            log.error("크롤링 실패: ${e.message}", e)
        }

        return articles
    }

    private fun findArticleElements(doc: Document): List<Element> {
        var elements = doc.select("ul[class*='PostList-module--container'] li")
        if (elements.isNotEmpty()) {
            log.debug("기존 셀렉터로 발견: ${elements.size}개")
            return elements.toList()
        }

        elements = doc.select("ul li:has(h1), ul li:has(h2), ul li:has(h3)")
        if (elements.isNotEmpty()) {
            log.debug("제목이 있는 li 요소로 발견: ${elements.size}개")
            return elements.toList()
        }

        elements = doc.select("article")
        if (elements.isNotEmpty()) {
            log.debug("article 태그로 발견: ${elements.size}개")
            return elements.toList()
        }

        elements = doc.select("div:has(a):has(h1), div:has(a):has(h2), div:has(a):has(h3)")
        if (elements.isNotEmpty()) {
            log.debug("링크와 제목이 있는 div로 발견: ${elements.size}개")
            return elements.toList()
        }

        elements = doc.select("li:has(a[href])")
        if (elements.isNotEmpty()) {
            log.debug("링크가 있는 li 요소로 발견: ${elements.size}개")
            return elements.toList().filter { isValidArticleElement(it) }
        }

        return emptyList()
    }

    private fun isValidArticleElement(element: Element): Boolean {
        // 유효한 글 요소인지 확인
        val hasTitle = element.select("h1, h2, h3, h4, h5, h6").isNotEmpty()
        val hasLink = element.select("a[href]").isNotEmpty()
        val hasContent = element.text().length > 10

        return hasTitle && hasLink && hasContent
    }

    private fun findArticleUrl(element: Element, baseUrl: String): String {
        try {
            val linkElement = element.selectFirst("a[href]")
            val href = linkElement.attr("href")

            return when {
                href.startsWith("http") -> href
                href.startsWith("/") -> baseUrl + href
                else -> "$baseUrl/$href"
            }
        } catch (e: Exception) {
            log.error("url 찾기 실패 with Base Url : $baseUrl")
            throw e
        }
    }

}