package com.server.noati.crawler

import com.server.noati.ai.CategoryAnalyzer
import com.server.noati.domain.Article
import com.server.noati.domain.Company
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Component
class OliveYoungBlogCrawler(
    private val categoryAnalyzer: CategoryAnalyzer,
) : BlogCrawler {

    private val log = org.slf4j.LoggerFactory.getLogger(OliveYoungBlogCrawler::class.java)

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

            log.info("발견된 글 개수: ${articleElements.size}")

            for ((index, articleElement) in articleElements.withIndex()) {
                try {
                    val article = parseArticle(articleElement, company, url)
                    if (article != null) {
                        articles.add(article)
                        log.debug("파싱 성공 ${index + 1}: ${article.title}")
                    }
                } catch (e: Exception) {
                    log.error("글 파싱 실패 ${index + 1}: ${e.message}", e)
                }
            }

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

    private fun parseArticle(element: Element, company: Company, baseUrl: String): Article? {
        try {
            // 제목 추출 - 여러 방법 시도
            val title = findTitle(element) ?: return null

            // 링크 추출
            val articleUrl = findArticleUrl(element, baseUrl) ?: return null

            // 요약 추출
            val summary = findSummary(element) ?: "요약 없음"

            // 날짜 추출
            val date = findDate(element) ?: LocalDate.now()



            return Article(
                company = company,
                title = title,
                summery = summary,
                postedDate = date,
                articleUrl = articleUrl,
                category = categoryAnalyzer.analyze(title, articleUrl)
            )

        } catch (e: Exception) {
            println("parseArticle 실패: ${e.message}")
            return null
        }
    }

    private fun findTitle(element: Element): String? {
        // 여러 방법으로 제목 찾기
        val selectors = listOf(
            "h1[class*='title'], h1[class*='Title']",  // 기존 방식
            "h2[class*='title'], h2[class*='Title']",
            "h3[class*='title'], h3[class*='Title']",
            "h1, h2, h3",  // 일반 제목 태그
            ".title, .Title",  // 클래스명에 title 포함
            "a[href]"  // 링크 텍스트를 제목으로 사용
        )

        for (selector in selectors) {
            val titleElement = element.selectFirst(selector)
            val title = titleElement?.text()?.trim()
            if (!title.isNullOrBlank() && title.length > 3) {
                return title
            }
        }

        return null
    }

    private fun findArticleUrl(element: Element, baseUrl: String): String? {
        val linkElement = element.selectFirst("a[href]") ?: return null
        val href = linkElement.attr("href")

        return when {
            href.startsWith("http") -> href
            href.startsWith("/") -> baseUrl + href
            else -> "$baseUrl/$href"
        }
    }

    private fun findSummary(element: Element): String? {
        val selectors = listOf(
            "p[class*='sub'], p[class*='summary'], p[class*='desc']",  // 기존 방식
            ".summary, .desc, .description, .excerpt",  // 일반적인 요약 클래스
            "p:not(:has(time)):not(:has(span[class*='date']))",  // 날짜가 아닌 p 태그
            "div:not(:has(h1)):not(:has(h2)):not(:has(h3))"  // 제목이 없는 div
        )

        for (selector in selectors) {
            val summaryElement = element.selectFirst(selector)
            val summary = summaryElement?.text()?.trim()
            if (!summary.isNullOrBlank() && summary.length > 10) {
                return summary
            }
        }

        // 전체 텍스트에서 제목을 제외한 부분 추출
        val fullText = element.text()
        val title = findTitle(element)
        return if (title != null && fullText.length > title.length + 10) {
            fullText.replace(title, "").trim().take(200)
        } else {
            null
        }
    }

    private fun findDate(element: Element): LocalDate? {
        val selectors = listOf(
            "span[class*='date'], span[class*='Date']",  // 기존 방식
            "time",  // time 태그
            ".date, .Date, .posted-date, .publish-date",  // 일반적인 날짜 클래스
            "span:matches(\\d{4}[-.]\\d{1,2}[-.]\\d{1,2})",  // 날짜 패턴 매칭
            "span:matches(\\d{1,2}[-.]\\d{1,2}[-.]\\d{4})"
        )

        for (selector in selectors) {
            val dateElement = element.selectFirst(selector)
            val dateText = dateElement?.text()?.trim()
            if (!dateText.isNullOrBlank()) {
                val parsedDate = parseDate(dateText)
                if (parsedDate != null) return parsedDate
            }
        }

        // 전체 텍스트에서 날짜 패턴 찾기
        val datePattern = Regex("\\d{4}[-.]\\d{1,2}[-.]\\d{1,2}")
        val match = datePattern.find(element.text())
        return match?.value?.let { parseDate(it) }
    }

    private fun parseDate(dateText: String): LocalDate? {
        val formatters = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("MM-dd-yyyy"),
            DateTimeFormatter.ofPattern("MM.dd.yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
        )

        val cleanedDate = dateText.replace(Regex("[^0-9./-]"), "")

        for (formatter in formatters) {
            try {
                return LocalDate.parse(cleanedDate, formatter)
            } catch (e: DateTimeParseException) {
                continue
            }
        }

        return null
    }
}