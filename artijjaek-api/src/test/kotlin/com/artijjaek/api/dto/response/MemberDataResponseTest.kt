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

        val categoryIds = listOf(1L, 2L, 3L)
        val companyIds = listOf(1L, 2L, 3L)

        // when
        val response = MemberDataResponse.of(member, companyIds, categoryIds)

        // then
        assertThat(response.email).isEqualTo("newuser@example.com")
        assertThat(response.nickname).isEqualTo("nickname")
        assertThat(response.companyIds.size).isEqualTo(3)
        assertThat(response.categoryIds.size).isEqualTo(3)
    }

}