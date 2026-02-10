package com.artijjaek.admin.service

import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.category.service.CategoryDomainService
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
}
