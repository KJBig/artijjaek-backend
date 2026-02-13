package com.artijjaek.api.dto.response

import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class MemberDataResponseTest {

    @Test
    @DisplayName("Member 엔티티와 회사, 카테고리 ID로 MemberDataResponse를 생성할 수 있다")
    fun ofTest() {
        // given
        val member = Member(
            email = "newuser@example.com",
            nickname = "nickname",
            uuidToken = "some-uuid-token",
            memberStatus = MemberStatus.ACTIVE
        )

        val categories = listOf(
            CategorySimpleDataResponse(1L, "카테고리1"),
            CategorySimpleDataResponse(2L, "카테고리2"),
            CategorySimpleDataResponse(3L, "카테고리3")
        )
        val companies = listOf(
            CompanySimpleDataResponse(1L, "회사1", "Company1", "https://example.com/logo1.png", "https://example.com/blog1"),
            CompanySimpleDataResponse(2L, "회사2", "Company2", "https://example.com/logo2.png", "https://example.com/blog2"),
            CompanySimpleDataResponse(3L, "회사3", "Company3", "https://example.com/logo3.png", "https://example.com/blog3")
        )

        // when
        val response = MemberDataResponse.of(member, companies, categories)

        // then
        assertThat(response.email).isEqualTo("newuser@example.com")
        assertThat(response.nickname).isEqualTo("nickname")
        assertThat(response.companies.size).isEqualTo(3)
        assertThat(response.categories.size).isEqualTo(3)
    }

}
