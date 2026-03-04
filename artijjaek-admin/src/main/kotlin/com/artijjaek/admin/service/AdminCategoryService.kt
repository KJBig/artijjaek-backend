package com.artijjaek.admin.service

import com.artijjaek.admin.dto.response.MemberOptionCategoryResponse
import com.artijjaek.admin.dto.response.TopSubscribedCategoryResponse
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.subscription.service.CategorySubscriptionDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminCategoryService(
    private val categoryDomainService: CategoryDomainService,
    private val categorySubscriptionDomainService: CategorySubscriptionDomainService,
) {
    companion object {
        private const val TOP_SUBSCRIBED_LIMIT = 5
    }

    @Transactional(readOnly = true)
    fun getMemberCategoryOptions(): List<MemberOptionCategoryResponse> {
        return categoryDomainService.findAll().map {
            MemberOptionCategoryResponse(
                categoryId = it.id!!,
                categoryName = it.name
            )
        }
    }

    @Transactional(readOnly = true)
    fun getTopSubscribedCategories(): List<TopSubscribedCategoryResponse> {
        val topCategories = categorySubscriptionDomainService.findTopSubscribedCategoriesWithTies(TOP_SUBSCRIBED_LIMIT)
        return denseRank(topCategories.map { it.subscriberCount }).mapIndexed { index, rank ->
            TopSubscribedCategoryResponse(
                rank = rank,
                categoryId = topCategories[index].categoryId,
                categoryName = topCategories[index].categoryName,
                subscriberCount = topCategories[index].subscriberCount
            )
        }
    }

    private fun denseRank(counts: List<Long>): List<Int> {
        var previousCount: Long? = null
        var rank = 0

        return counts.mapIndexed { index, count ->
            if (previousCount == null || previousCount != count) {
                rank = index + 1
                previousCount = count
            }
            rank
        }
    }
}
