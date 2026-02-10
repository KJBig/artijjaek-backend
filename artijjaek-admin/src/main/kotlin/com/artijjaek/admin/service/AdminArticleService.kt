package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostArticleRequest
import com.artijjaek.admin.dto.request.PutArticleRequest
import com.artijjaek.admin.dto.response.ArticleCategoryResponse
import com.artijjaek.admin.dto.response.ArticleDetailResponse
import com.artijjaek.admin.dto.response.ArticleListPageResponse
import com.artijjaek.admin.dto.response.ArticleCompanyResponse
import com.artijjaek.admin.dto.response.ArticleSimpleResponse
import com.artijjaek.admin.dto.response.PostArticleResponse
import com.artijjaek.admin.enums.ArticleListSortBy
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.ARTICLE_NOT_FOUND_ERROR
import com.artijjaek.core.common.error.ErrorCode.CATEGORY_NOT_FOUND_ERROR
import com.artijjaek.core.common.error.ErrorCode.COMPANY_NOT_FOUND_ERROR
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.enums.ArticleSortBy
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.company.service.CompanyDomainService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminArticleService(
    private val articleDomainService: ArticleDomainService,
    private val companyDomainService: CompanyDomainService,
    private val categoryDomainService: CategoryDomainService,
) {

    @Transactional
    fun createArticle(request: PostArticleRequest): PostArticleResponse {
        val company = companyDomainService.findAllOrByIds(listOf(request.companyId)).firstOrNull()
            ?: throw ApplicationException(COMPANY_NOT_FOUND_ERROR)
        val category = request.categoryId?.let { categoryId ->
            categoryDomainService.findAllOrByIds(listOf(categoryId)).firstOrNull()
                ?: throw ApplicationException(CATEGORY_NOT_FOUND_ERROR)
        }

        val article = Article(
            company = company,
            category = category,
            title = request.title,
            description = request.description,
            image = request.image,
            link = request.link
        )
        articleDomainService.save(article)

        return PostArticleResponse(articleId = article.id!!)
    }

    @Transactional(readOnly = true)
    fun getArticleDetail(articleId: Long): ArticleDetailResponse {
        val article = articleDomainService.findById(articleId)
            ?: throw ApplicationException(ARTICLE_NOT_FOUND_ERROR)

        return ArticleDetailResponse(
            articleId = article.id!!,
            title = article.title,
            description = article.description,
            image = article.image,
            link = article.link,
            company = ArticleCompanyResponse(
                companyId = article.company.id!!,
                companyNameKr = article.company.nameKr,
                logo = article.company.logo
            ),
            category = article.category?.let {
                ArticleCategoryResponse(
                    categoryId = it.id!!,
                    categoryName = it.name
                )
            },
            createdAt = article.createdAt!!
        )
    }

    @Transactional
    fun updateArticle(articleId: Long, request: PutArticleRequest) {
        val article = articleDomainService.findById(articleId)
            ?: throw ApplicationException(ARTICLE_NOT_FOUND_ERROR)

        val company = companyDomainService.findAllOrByIds(listOf(request.companyId)).firstOrNull()
            ?: throw ApplicationException(COMPANY_NOT_FOUND_ERROR)
        val category = request.categoryId?.let { categoryId ->
            categoryDomainService.findAllOrByIds(listOf(categoryId)).firstOrNull()
                ?: throw ApplicationException(CATEGORY_NOT_FOUND_ERROR)
        }

        article.title = request.title
        article.description = request.description
        article.image = request.image
        article.link = request.link
        article.company = company
        article.changeCategory(category)

        articleDomainService.save(article)
    }

    @Transactional(readOnly = true)
    fun searchArticles(
        pageable: Pageable,
        companyId: Long?,
        categoryId: Long?,
        title: String?,
        sortBy: ArticleListSortBy,
        sortDirection: Sort.Direction,
    ): ArticleListPageResponse {
        val articlePage = articleDomainService.findWithCondition(
            pageable = PageRequest.of(pageable.pageNumber, pageable.pageSize),
            companyId = companyId,
            categoryId = categoryId,
            titleKeyword = title?.trim()?.takeIf { it.isNotBlank() },
            sortBy = sortBy.toArticleSortBy(),
            sortDirection = sortDirection
        )

        val content = articlePage.content.map {
            ArticleSimpleResponse(
                articleId = it.id!!,
                title = it.title,
                company = ArticleCompanyResponse(
                    companyId = it.company.id!!,
                    companyNameKr = it.company.nameKr,
                    logo = it.company.logo
                ),
                categoryName = it.category?.name,
                link = it.link,
                image = it.image,
                description = it.description,
                createdAt = it.createdAt!!
            )
        }

        return ArticleListPageResponse(
            pageNumber = articlePage.number,
            totalCount = articlePage.totalElements,
            hasNext = articlePage.hasNext(),
            content = content
        )
    }

    private fun ArticleListSortBy.toArticleSortBy(): ArticleSortBy {
        return when (this) {
            ArticleListSortBy.REGISTER_DATE -> ArticleSortBy.CREATED_AT
            ArticleListSortBy.TITLE -> ArticleSortBy.TITLE
            ArticleListSortBy.COMPANY -> ArticleSortBy.COMPANY
            ArticleListSortBy.CATEGORY -> ArticleSortBy.CATEGORY
        }
    }
}
