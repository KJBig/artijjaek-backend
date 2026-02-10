package com.artijjaek.admin.service

import com.artijjaek.admin.dto.response.ArticleListPageResponse
import com.artijjaek.admin.dto.response.ArticleCompanyResponse
import com.artijjaek.admin.dto.response.ArticleSimpleResponse
import com.artijjaek.admin.enums.ArticleListSortBy
import com.artijjaek.core.domain.article.enums.ArticleSortBy
import com.artijjaek.core.domain.article.service.ArticleDomainService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminArticleService(
    private val articleDomainService: ArticleDomainService,
) {

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
