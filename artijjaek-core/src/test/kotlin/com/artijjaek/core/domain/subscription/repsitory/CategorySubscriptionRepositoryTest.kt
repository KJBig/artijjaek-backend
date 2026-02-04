package com.artijjaek.core.domain.subscription.repsitory

import com.artijjaek.core.config.TestConfig
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.category.repository.CategoryRepository
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.repository.MemberRepository
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.repository.CategorySubscriptionRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test


@DataJpaTest
@ContextConfiguration(classes = [TestConfig::class])
@ActiveProfiles("test")
class CategorySubscriptionRepositoryTest {

    @Autowired
    lateinit var categorySubscriptionRepository: CategorySubscriptionRepository

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @AfterEach
    fun clear() {
        categorySubscriptionRepository.deleteAll()
        memberRepository.deleteAll()
        categoryRepository.deleteAll()
    }

    @Test
    @DisplayName("멤버로 카테고리 구독 조회")
    fun findAllByMemberFetchCategoryTest() {
        // given
        val category = Category(
            name = "카테고리",
            publishType = PublishType.PUBLISH
        )
        val saveCategory = categoryRepository.save(category)

        val member = Member(
            email = "test@example.com",
            nickname = "nickname",
            uuidToken = "some-uuid-token",
            memberStatus = MemberStatus.ACTIVE
        )
        val saveMember = memberRepository.save(member)

        val categorySubscription = CategorySubscription(member = saveMember, category = saveCategory)
        categorySubscriptionRepository.save(categorySubscription)


        // when
        val result = categorySubscriptionRepository.findAllByMemberFetchCategory(saveMember)


        // then
        Assertions.assertThat(result.size).isEqualTo(1)
        Assertions.assertThat(result[0].category.name).isEqualTo("카테고리")
    }

}