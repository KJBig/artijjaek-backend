package com.artijjaek.api.service

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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CategoryServiceTest {

    @InjectMockKs
    lateinit var categoryService: CategoryService

    @MockK
    lateinit var categoryDomainService: CategoryDomainService

    @Test
    @DisplayName("카테고리 목록을 조회할 수 있다")
    fun searchCategoryListTest() {
        // given
        val pageRequest = PageRequest.of(0, 1)

        val category = Category(
            id = 1L,
            name = "카테고리",
            publishType = PublishType.PUBLISH
        )

        val categoryPage = PageImpl(
            listOf(category),
            pageRequest,
            1
        )

        every { categoryDomainService.findPublishableCategoryWithPageable(pageRequest) }
            .returns(categoryPage)


        // when
        val response = categoryService.searchCategoryList(pageRequest)

        // then
        assertThat(response.pageNumber).isEqualTo(0)
        assertThat(response.hasNext).isEqualTo(false)
        assertThat(response.content.size).isEqualTo(1)
        assertThat(response.content.get(0).categoryId).isEqualTo(1L)
    }

}