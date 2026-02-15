package com.artijjaek.core.domain.member.entity

import com.artijjaek.core.domain.member.enums.MemberStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class MemberTest {

    @Test
    @DisplayName("사용자의 상태를 변경할 수 있다")
    fun changeMemberStatusTest() {
        // given
        val member = Member(
            email = "test@example.com",
            nickname = "nickname",
            uuidToken = "some-uuid-token",
            memberStatus = MemberStatus.ACTIVE
        )

        // when
        member.changeMemberStatus(MemberStatus.DELETED)

        // then
        assertThat(member.memberStatus).isEqualTo(MemberStatus.DELETED)
    }

    @Test
    @DisplayName("사용자의 이메일을 변경할 수 있다")
    fun changeEmailTest() {
        // given
        val member = Member(
            email = "test@example.com",
            nickname = "nickname",
            uuidToken = "some-uuid-token",
            memberStatus = MemberStatus.ACTIVE
        )

        // when
        member.changeEmail(null)

        // then
        assertThat(member.email).isNull()
    }

    @Test
    @DisplayName("사용자의 닉네임을 변경할 수 있다")
    fun changeNicknameTest() {
        // given
        val member = Member(
            email = "test@example.com",
            nickname = "nickname",
            uuidToken = "some-uuid-token",
            memberStatus = MemberStatus.ACTIVE
        )

        // when
        member.changeNickname("nick")

        // then
        assertThat(member.nickname).isEqualTo("nick")
    }

}