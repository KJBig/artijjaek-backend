package com.artijjaek.core.article.entity

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class ArticleTest {

    @Test
    @DisplayName("아티클의 카테고리를 변경할 수 있다.")
    fun changeCategoryTest() {
        // given
        val article = Article(
            title = "아티클",
            link = "http://example.com/article",
            company = mockk(),
            category = null,
            description = null,
            image = null
        )

        val newCategory = Category(
            id = 1L,
            name = "카테고리",
            publishType = PublishType.PUBLISH
        )

        // when
        article.changeCategory(newCategory)

        // then
        Assertions.assertThat(article.category!!.name).isEqualTo("카테고리")
    }
    
}