package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostArticleRequest
import com.artijjaek.admin.dto.request.PutArticleRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import com.artijjaek.admin.enums.ArticleListSortBy
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.enums.ArticleSortBy
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AdminArticleServiceTest {

    @InjectMockKs
    lateinit var adminArticleService: AdminArticleService

    @MockK
    lateinit var articleDomainService: ArticleDomainService

    @MockK
    lateinit var companyDomainService: CompanyDomainService

    @MockK
    lateinit var categoryDomainService: CategoryDomainService

    @Test
    @DisplayName("아티클을 등록한다")
    fun createArticleTest() {
        // given
        val company = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo",
            baseUrl = "baseUrl",
            crawlUrl = "crawlUrl",
            crawlAvailability = true
        )
        val category = Category(
            id = 20L,
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val request = PostArticleRequest(
            title = "신규 아티클",
            description = "설명",
            image = "https://image.example.com/new.png",
            link = "https://example.com/article/new",
            companyId = 10L,
            categoryId = 20L
        )

        every { companyDomainService.findAllOrByIds(listOf(10L)) } returns listOf(company)
        every { categoryDomainService.findAllOrByIds(listOf(20L)) } returns listOf(category)
        every { articleDomainService.save(any()) } answers {
            firstArg<Article>().id = 101L
            Unit
        }

        // when
        val result = adminArticleService.createArticle(request)

        // then
        assertThat(result.articleId).isEqualTo(101L)
        verify(exactly = 1) {
            articleDomainService.save(
                match {
                    it.title == "신규 아티클" &&
                        it.company.id == 10L &&
                        it.category?.id == 20L &&
                        it.link == "https://example.com/article/new"
                }
            )
        }
    }

    @Test
    @DisplayName("아티클 등록 시 회사가 없으면 예외가 발생한다")
    fun createArticleCompanyNotFoundTest() {
        // given
        val request = PostArticleRequest(
            title = "신규 아티클",
            description = null,
            image = null,
            link = "https://example.com/article/new",
            companyId = 999L,
            categoryId = null
        )
        every { companyDomainService.findAllOrByIds(listOf(999L)) } returns emptyList()

        // when
        val exception = assertThrows<ApplicationException> {
            adminArticleService.createArticle(request)
        }

        // then
        assertThat(exception.code).isEqualTo(ErrorCode.COMPANY_NOT_FOUND_ERROR.code)
    }

    @Test
    @DisplayName("아티클 상세 정보를 조회한다")
    fun getArticleDetailTest() {
        // given
        val company = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo",
            baseUrl = "baseUrl",
            crawlUrl = "crawlUrl",
            crawlAvailability = true
        )
        val category = Category(
            id = 20L,
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val article = Article(
            id = 100L,
            company = company,
            category = category,
            title = "백엔드 개발자 채용",
            description = "설명",
            image = "https://image.example.com/100.png",
            link = "https://example.com/article/100"
        ).apply { createdAt = LocalDateTime.of(2025, 1, 10, 12, 0) }

        every { articleDomainService.findById(100L) } returns article

        // when
        val result = adminArticleService.getArticleDetail(100L)

        // then
        assertThat(result.articleId).isEqualTo(100L)
        assertThat(result.company.companyId).isEqualTo(10L)
        assertThat(result.category!!.categoryName).isEqualTo("백엔드")
    }

    @Test
    @DisplayName("아티클 정보를 수정한다")
    fun updateArticleTest() {
        // given
        val oldCompany = Company(
            id = 1L,
            nameKr = "구회사",
            nameEn = "OldCompany",
            logo = "old-logo",
            baseUrl = "old-base",
            crawlUrl = "old-crawl",
            crawlAvailability = true
        )
        val oldCategory = Category(
            id = 2L,
            name = "구카테고리",
            publishType = PublishType.PUBLISH
        )
        val article = Article(
            id = 100L,
            company = oldCompany,
            category = oldCategory,
            title = "old title",
            description = "old desc",
            image = "old-image",
            link = "old-link"
        )
        val newCompany = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo",
            baseUrl = "baseUrl",
            crawlUrl = "crawlUrl",
            crawlAvailability = true
        )
        val newCategory = Category(
            id = 20L,
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val request = PutArticleRequest(
            title = "new title",
            description = "new desc",
            image = "new-image",
            link = "new-link",
            companyId = 10L,
            categoryId = 20L
        )

        every { articleDomainService.findById(100L) } returns article
        every { companyDomainService.findAllOrByIds(listOf(10L)) } returns listOf(newCompany)
        every { categoryDomainService.findAllOrByIds(listOf(20L)) } returns listOf(newCategory)
        every { articleDomainService.save(article) } returns Unit

        // when
        adminArticleService.updateArticle(100L, request)

        // then
        assertThat(article.title).isEqualTo("new title")
        assertThat(article.description).isEqualTo("new desc")
        assertThat(article.image).isEqualTo("new-image")
        assertThat(article.link).isEqualTo("new-link")
        assertThat(article.company.id).isEqualTo(10L)
        assertThat(article.category!!.id).isEqualTo(20L)
        verify(exactly = 1) { articleDomainService.save(article) }
    }

    @Test
    @DisplayName("아티클 수정 시 회사가 없으면 예외가 발생한다")
    fun updateArticleCompanyNotFoundTest() {
        // given
        val article = Article(
            id = 100L,
            company = Company(
                id = 1L,
                nameKr = "구회사",
                nameEn = "OldCompany",
                logo = "old-logo",
                baseUrl = "old-base",
                crawlUrl = "old-crawl",
                crawlAvailability = true
            ),
            category = null,
            title = "title",
            description = null,
            image = null,
            link = "link"
        )
        val request = PutArticleRequest(
            title = "new title",
            description = null,
            image = null,
            link = "new-link",
            companyId = 999L,
            categoryId = null
        )
        every { articleDomainService.findById(100L) } returns article
        every { companyDomainService.findAllOrByIds(listOf(999L)) } returns emptyList()

        // when
        val exception = assertThrows<ApplicationException> {
            adminArticleService.updateArticle(100L, request)
        }

        // then
        assertThat(exception.code).isEqualTo(ErrorCode.COMPANY_NOT_FOUND_ERROR.code)
    }

    @Test
    @DisplayName("아티클 목록을 페이지로 조회한다")
    fun searchArticlesTest() {
        // given
        val pageable = PageRequest.of(0, 5)
        val company = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo",
            baseUrl = "baseUrl",
            crawlUrl = "crawlUrl",
            crawlAvailability = true
        )
        val category = Category(
            id = 20L,
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val article = Article(
            id = 100L,
            company = company,
            category = category,
            title = "백엔드 개발자 채용",
            description = "설명",
            image = "https://image.example.com/100.png",
            link = "https://example.com/article/100"
        ).apply { createdAt = LocalDateTime.of(2025, 1, 10, 12, 0) }
        val page = PageImpl(listOf(article), pageable, 12)

        every {
            articleDomainService.findWithCondition(
                pageable = pageable,
                companyId = 10L,
                categoryId = 20L,
                titleKeyword = "개발자",
                sortBy = ArticleSortBy.CREATED_AT,
                sortDirection = Sort.Direction.DESC
            )
        } returns page

        // when
        val result = adminArticleService.searchArticles(
            pageable = pageable,
            companyId = 10L,
            categoryId = 20L,
            title = " 개발자 ",
            sortBy = ArticleListSortBy.REGISTER_DATE,
            sortDirection = Sort.Direction.DESC
        )

        // then
        assertThat(result.pageNumber).isEqualTo(0)
        assertThat(result.totalCount).isEqualTo(12)
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].articleId).isEqualTo(100L)
        assertThat(result.content[0].company.companyNameKr).isEqualTo("회사A")
        assertThat(result.content[0].company.logo).isEqualTo("logo")
        assertThat(result.content[0].categoryName).isEqualTo("백엔드")
        assertThat(result.content[0].image).isEqualTo("https://image.example.com/100.png")
        assertThat(result.content[0].description).isEqualTo("설명")
        verify(exactly = 1) {
            articleDomainService.findWithCondition(
                pageable = pageable,
                companyId = 10L,
                categoryId = 20L,
                titleKeyword = "개발자",
                sortBy = ArticleSortBy.CREATED_AT,
                sortDirection = Sort.Direction.DESC
            )
        }
    }
}
