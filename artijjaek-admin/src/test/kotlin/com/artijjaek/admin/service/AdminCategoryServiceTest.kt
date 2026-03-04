package com.artijjaek.admin.service

import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.subscription.dto.TopSubscribedCategoryCount
import com.artijjaek.core.domain.subscription.service.CategorySubscriptionDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AdminCategoryServiceTest {

    @InjectMockKs
    lateinit var adminCategoryService: AdminCategoryService

    @MockK
    lateinit var categoryDomainService: CategoryDomainService

    @MockK
    lateinit var categorySubscriptionDomainService: CategorySubscriptionDomainService

    @Test
    @DisplayName("회원 편집 드롭다운 카테고리 옵션을 조회한다")
    fun getMemberCategoryOptionsTest() {
        // given
        val categoryA = Category(
            id = 20L,
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val categoryB = Category(
            id = 21L,
            name = "프론트",
            publishType = PublishType.PUBLISH
        )
        every { categoryDomainService.findAll() } returns listOf(categoryA, categoryB)

        // when
        val result = adminCategoryService.getMemberCategoryOptions()

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].categoryId).isEqualTo(20L)
        assertThat(result[1].categoryName).isEqualTo("프론트")
    }

    @Test
    @DisplayName("구독자들이 많이 구독한 카테고리 Top 목록을 동점 포함으로 조회한다")
    fun getTopSubscribedCategoriesTest() {
        // given
        every { categorySubscriptionDomainService.findTopSubscribedCategoriesWithTies(5) } returns listOf(
            TopSubscribedCategoryCount(categoryId = 1L, categoryName = "백엔드", subscriberCount = 12),
            TopSubscribedCategoryCount(categoryId = 2L, categoryName = "프론트", subscriberCount = 9),
            TopSubscribedCategoryCount(categoryId = 3L, categoryName = "인프라", subscriberCount = 9),
        )

        // when
        val result = adminCategoryService.getTopSubscribedCategories()

        // then
        assertThat(result).hasSize(3)
        assertThat(result[0].rank).isEqualTo(1)
        assertThat(result[1].rank).isEqualTo(2)
        assertThat(result[2].rank).isEqualTo(2)
    }
}
