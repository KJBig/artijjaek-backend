package com.artijjaek.api.dto.response

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class CategorySimpleDataResponseTest {

    @Test
    @DisplayName("Category 엔티티로부터 CategorySimpleDataResponse를 생성할 수 있다")
    fun fromTest() {
        // given
        val category = Category(
            id = 1L,
            name = "카테고리",
            publishType = PublishType.PUBLISH
        )

        // when
        val response = CategorySimpleDataResponse.from(category)

        // then
        assertThat(response.categoryId).isEqualTo(1L)
        assertThat(response.categoryName).isEqualTo("카테고리")
    }

    @Test
    @DisplayName("Id가 없는 Category 엔티티로부터 CategorySimpleDataResponse를 생성할 수 없다")
    fun fromTest_NoCategoryId() {
        // given
        val category = Category(
            name = "카테고리",
            publishType = PublishType.PUBLISH
        )

        // when
        val exception = assertThrows(ApplicationException::class.java) {
            CategorySimpleDataResponse.from(category)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.CATEGORY_ID_MISSING_ERROR.code)
    }
}