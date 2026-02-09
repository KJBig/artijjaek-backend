package com.artijjaek.core.domain.member.repository

import com.artijjaek.core.config.TestConfig
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberSortBy
import com.artijjaek.core.domain.member.enums.MemberStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [TestConfig::class])
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @AfterEach
    fun clear() {
        memberRepository.deleteAll()
    }

    @Test
    @DisplayName("회원 목록을 상태/키워드 조건으로 조회한다")
    fun findWithConditionByFilterAndKeywordTest() {
        // given
        memberRepository.save(
            Member(
                email = "john.doe@example.com",
                nickname = "John",
                uuidToken = "token-1",
                memberStatus = MemberStatus.ACTIVE
            )
        )
        memberRepository.save(
            Member(
                email = "jane.doe@example.com",
                nickname = "Jane",
                uuidToken = "token-2",
                memberStatus = MemberStatus.ACTIVE
            )
        )
        memberRepository.save(
            Member(
                email = "john.deleted@example.com",
                nickname = "John Deleted",
                uuidToken = "token-3",
                memberStatus = MemberStatus.DELETED
            )
        )

        // when
        val result = memberRepository.findWithCondition(
            pageable = PageRequest.of(0, 10),
            memberStatus = MemberStatus.ACTIVE,
            nicknameKeyword = "John",
            emailKeyword = null,
            sortBy = MemberSortBy.NICKNAME,
            sortDirection = Sort.Direction.ASC
        )

        // then
        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content[0].nickname).isEqualTo("John")
        assertThat(result.content[0].memberStatus).isEqualTo(MemberStatus.ACTIVE)
    }

    @Test
    @DisplayName("회원 목록을 이메일 오름차순으로 정렬한다")
    fun findWithConditionSortByEmailTest() {
        // given
        memberRepository.save(
            Member(
                email = "charlie@example.com",
                nickname = "Charlie",
                uuidToken = "token-1",
                memberStatus = MemberStatus.ACTIVE
            )
        )
        memberRepository.save(
            Member(
                email = "alpha@example.com",
                nickname = "Alpha",
                uuidToken = "token-2",
                memberStatus = MemberStatus.ACTIVE
            )
        )
        memberRepository.save(
            Member(
                email = "bravo@example.com",
                nickname = "Bravo",
                uuidToken = "token-3",
                memberStatus = MemberStatus.ACTIVE
            )
        )

        // when
        val result = memberRepository.findWithCondition(
            pageable = PageRequest.of(0, 10),
            memberStatus = null,
            nicknameKeyword = null,
            emailKeyword = null,
            sortBy = MemberSortBy.EMAIL,
            sortDirection = Sort.Direction.ASC
        )

        // then
        assertThat(result.content.map { it.email })
            .containsExactly("alpha@example.com", "bravo@example.com", "charlie@example.com")
    }

    @Test
    @DisplayName("회원 목록을 상태 기준으로 정렬하고 상태별 카운트를 조회한다")
    fun findWithConditionSortByStatusAndCountTest() {
        // given
        memberRepository.save(
            Member(
                email = "active1@example.com",
                nickname = "Active1",
                uuidToken = "token-1",
                memberStatus = MemberStatus.ACTIVE
            )
        )
        memberRepository.save(
            Member(
                email = "deleted1@example.com",
                nickname = "Deleted1",
                uuidToken = "token-2",
                memberStatus = MemberStatus.DELETED
            )
        )
        memberRepository.save(
            Member(
                email = "active2@example.com",
                nickname = "Active2",
                uuidToken = "token-3",
                memberStatus = MemberStatus.ACTIVE
            )
        )

        // when
        val result = memberRepository.findWithCondition(
            pageable = PageRequest.of(0, 10),
            memberStatus = null,
            nicknameKeyword = null,
            emailKeyword = null,
            sortBy = MemberSortBy.STATUS,
            sortDirection = Sort.Direction.ASC
        )
        val allCount = memberRepository.countByMemberStatus(null)
        val activeCount = memberRepository.countByMemberStatus(MemberStatus.ACTIVE)
        val deletedCount = memberRepository.countByMemberStatus(MemberStatus.DELETED)

        // then
        assertThat(result.content.take(2).map { it.memberStatus })
            .containsOnly(MemberStatus.ACTIVE)
        assertThat(result.content.last().memberStatus).isEqualTo(MemberStatus.DELETED)
        assertThat(allCount).isEqualTo(3)
        assertThat(activeCount).isEqualTo(2)
        assertThat(deletedCount).isEqualTo(1)
    }
}
