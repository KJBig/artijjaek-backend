package com.artijjaek.core.category.repository

import com.artijjaek.core.config.TestConfig
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.category.repository.CategoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test

@DataJpaTest
@ContextConfiguration(classes = [TestConfig::class])
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @AfterEach
    fun clear() {
        categoryRepository.deleteAll()
    }

    @Test
    @DisplayName("ID 목록으로 카테고리 조회 또는 전체 조회 - ID 목록이 있을 경우")
    fun findByIdsOrAllTest_IdList() {
        // given
        val backCategory = Category(
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val frontCategory = Category(
            name = "프론트엔드",
            publishType = PublishType.PUBLISH
        )
        val saveCategory = categoryRepository.save(backCategory)
        categoryRepository.save(frontCategory)

        val categoryIds = listOf(saveCategory.id!!)


        // when
        val result = categoryRepository.findByIdsOrAll(categoryIds)

        // then
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].name).isEqualTo("백엔드")
    }

    @Test
    @DisplayName("ID 목록으로 카테고리 조회 또는 전체 조회 - ID 목록이 없을 경우")
    fun findByIdsOrAllTest_All() {
        // given
        val backCategory = Category(
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val frontCategory = Category(
            name = "프론트엔드",
            publishType = PublishType.PUBLISH
        )
        categoryRepository.save(backCategory)
        categoryRepository.save(frontCategory)


        // when
        val result = categoryRepository.findByIdsOrAll(emptyList())

        // then
        assertThat(result.size).isEqualTo(2)
    }

    @Test
    @DisplayName("배포 가능한 카테고리의 페이지를 조회한다 - 페이징")
    fun findPublishCategoryWithPageableTest_Pageable() {
        // given
        val categories = mutableListOf<Category>()
        for (i in 1..2) {
            val category = Category(
                name = "카테고리${i}",
                publishType = PublishType.PUBLISH
            )
            categories.add(category)
        }
        categoryRepository.saveAll(categories)

        val pageRequest = PageRequest.of(0, 1)


        // when
        val result = categoryRepository.findPublishCategoryWithPageable(pageRequest)

        // then
        assertThat(result.content.size).isEqualTo(1)
    }

    @Test
    @DisplayName("배포 가능한 카테고리의 페이지를 조회한다 - 퍼블리시")
    fun findPublishCategoryWithPageableTest_Publish() {
        // given
        val categories = mutableListOf<Category>()
        for (i in 1..2) {
            val category = Category(
                name = "카테고리${i}",
                publishType = PublishType.PUBLISH
            )
            categories.add(category)
        }
        val nonPublishCategory = Category(
            name = "기타",
            publishType = PublishType.BLOCK
        )
        categoryRepository.saveAll(categories)
        categoryRepository.save(nonPublishCategory)

        val pageRequest = PageRequest.of(0, 10)


        // when
        val result = categoryRepository.findPublishCategoryWithPageable(pageRequest)

        // then
        assertThat(result.content.size).isEqualTo(2)
    }

}