package com.noati.batch.crawler.blog

import com.noati.batch.crawler.UrlDataCrawler
import com.noati.core.domain.Article
import com.noati.core.domain.Company
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URLDecoder

@Component
class DaangnTeamBlogCrawler(
    private val urlDataCrawler: UrlDataCrawler,
) : BlogCrawler {

    private val log = LoggerFactory.getLogger(DaangnTeamBlogCrawler::class.java)

    override val getBlogName: String
        get() = "DAANGN"

    override fun crawl(company: Company): List<Article> {
        val url: String = company.blogUrl
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

            return articles.distinctBy { it.link }

        } catch (e: Exception) {
            log.error("크롤링 실패: ${e.message}", e)
        }

        return articles
    }

    private fun findArticleElements(doc: Document): List<Element> {
        // 1) div[data-href] 요소 찾기 (새로운 구조)
        var elements = doc.select("div[href]")
        if (elements.isNotEmpty()) {
            log.debug("div[href]로 발견: ${elements.size}개")
            return filterNotValidUrl(elements)
        }

        // 2) 기존의 a[data-href]
        elements = doc.select("a[href]")
        if (elements.isNotEmpty()) {
            log.debug("a[data-href]로 발견: ${elements.size}개")
            return filterNotValidUrl(elements)
        }

        // 3) 모든 data-href 속성을 가진 요소 (포괄적)
        elements = doc.select("[href]")
        if (elements.isNotEmpty()) {
            log.debug("[data-href]로 발견: ${elements.size}개")
            return filterNotValidUrl(elements)
        }

        // 4) article 태그 내의 data-href 링크
        elements = doc.select("article [href]")
        if (elements.isNotEmpty()) {
            log.debug("article [data-href]로 발견: ${elements.size}개")
            return filterNotValidUrl(elements)
        }

        // 5) 제목 태그를 가진 컨테이너 내 data-href 링크
        elements = doc.select("div[href][role=link]")
        if (elements.isNotEmpty()) {
            log.debug("제목 포함 div [data-href]로 발견: ${elements.size}개")
            return filterNotValidUrl(elements)
        }

        return emptyList()
    }

    private fun filterNotValidUrl(elements: Elements): List<Element> {
        return elements.stream().filter { element ->
            val href = element.attr("href")
            href.contains("/blog/archive/")
        }.distinct().limit(10).toList()
    }

    private fun findArticleUrl(element: Element, baseUrl: String): String {
        try {
            val href = element.attr("abs:href").ifBlank { element.attr("href") }
            var decode = URLDecoder.decode(href, "UTF-8")
            if (decode.contains("�")) {
                decode = href.replace("�", "")
            }

            return when {
                decode.startsWith("http") -> decode
                decode.startsWith("/") -> baseUrl.removeSuffix("/") + decode
                else -> "$baseUrl/${decode.removePrefix("/")}"
            }
        } catch (e: Exception) {
            log.error("url 찾기 실패 with Base Url : $baseUrl")
            throw e
        }
    }
}