package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PutMemberRequest
import com.artijjaek.admin.enums.MemberListSearchType
import com.artijjaek.admin.enums.MemberListSortBy
import com.artijjaek.admin.enums.MemberStatusFilter
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberSortBy
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberDomainService
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.service.CategorySubscriptionDomainService
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AdminMemberServiceTest {

    @InjectMockKs
    lateinit var adminMemberService: AdminMemberService

    @MockK
    lateinit var memberDomainService: MemberDomainService

    @MockK
    lateinit var companyDomainService: CompanyDomainService

    @MockK
    lateinit var categoryDomainService: CategoryDomainService

    @MockK
    lateinit var companySubscriptionDomainService: CompanySubscriptionDomainService

    @MockK
    lateinit var categorySubscriptionDomainService: CategorySubscriptionDomainService

    @Test
    @DisplayName("회원 상세 정보를 조회한다")
    fun getMemberDetailTest() {
        // given
        val member = Member(
            id = 1L,
            email = "john.doe@example.com",
            nickname = "John Doe",
            uuidToken = "token-1",
            memberStatus = MemberStatus.ACTIVE
        )
        val company = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo",
            baseUrl = "baseUrl",
            crawlUrl = "crawlUrl",
            crawlAvailability = true
        )
        val category = Category(
            id = 20L,
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val companySubscription = CompanySubscription(member = member, company = company)
        val categorySubscription = CategorySubscription(member = member, category = category)

        every { memberDomainService.findById(1L) } returns member
        every { companySubscriptionDomainService.findAllByMemberFetchCompany(member) } returns listOf(companySubscription)
        every { categorySubscriptionDomainService.findAllByMemberFetchCategory(member) } returns listOf(categorySubscription)

        // when
        val result = adminMemberService.getMemberDetail(1L)

        // then
        assertThat(result.memberId).isEqualTo(1L)
        assertThat(result.email).isEqualTo("john.doe@example.com")
        assertThat(result.subscribedCompanies).hasSize(1)
        assertThat(result.subscribedCompanies[0].companyNameKr).isEqualTo("회사A")
        assertThat(result.subscribedCategories).hasSize(1)
        assertThat(result.subscribedCategories[0].categoryName).isEqualTo("백엔드")
    }

    @Test
    @DisplayName("회원 정보와 구독 정보를 수정한다")
    fun updateMemberTest() {
        // given
        val member = Member(
            id = 1L,
            email = "old@example.com",
            nickname = "old",
            uuidToken = "token-1",
            memberStatus = MemberStatus.ACTIVE
        )
        val companyA = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo-a",
            baseUrl = "base-a",
            crawlUrl = "crawl-a",
            crawlAvailability = true
        )
        val companyB = Company(
            id = 11L,
            nameKr = "회사B",
            nameEn = "CompanyB",
            logo = "logo-b",
            baseUrl = "base-b",
            crawlUrl = "crawl-b",
            crawlAvailability = true
        )
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
        val request = PutMemberRequest(
            email = "new@example.com",
            nickname = "new",
            companyIds = listOf(10L, 11L, 10L),
            categoryIds = listOf(20L, 21L, 20L)
        )

        every { memberDomainService.findById(1L) } returns member
        every { memberDomainService.save(member) } returns member
        every { companyDomainService.findAllOrByIds(listOf(10L, 11L)) } returns listOf(companyA, companyB)
        every { categoryDomainService.findAllOrByIds(listOf(20L, 21L)) } returns listOf(categoryA, categoryB)
        every { companySubscriptionDomainService.deleteAllByMemberId(1L) } returns Unit
        every { categorySubscriptionDomainService.deleteAllByMemberId(1L) } returns Unit
        every { companySubscriptionDomainService.saveAll(any()) } returns Unit
        every { categorySubscriptionDomainService.saveAll(any()) } returns Unit

        // when
        adminMemberService.updateMember(1L, request)

        // then
        assertThat(member.email).isEqualTo("new@example.com")
        assertThat(member.nickname).isEqualTo("new")
        verify(exactly = 1) { memberDomainService.save(member) }
        verify(exactly = 1) { companyDomainService.findAllOrByIds(listOf(10L, 11L)) }
        verify(exactly = 1) { categoryDomainService.findAllOrByIds(listOf(20L, 21L)) }
        verify(exactly = 1) { companySubscriptionDomainService.deleteAllByMemberId(1L) }
        verify(exactly = 1) { categorySubscriptionDomainService.deleteAllByMemberId(1L) }
        verify(exactly = 1) { companySubscriptionDomainService.saveAll(match { it.size == 2 }) }
        verify(exactly = 1) { categorySubscriptionDomainService.saveAll(match { it.size == 2 }) }
    }

    @Test
    @DisplayName("회원 목록을 닉네임 검색, 상태 필터, 구독일 정렬로 조회한다")
    fun searchMembersTest() {
        // given
        val pageable = PageRequest.of(0, 5)
        val firstMember = Member(
            id = 1L,
            email = "john.doe@example.com",
            nickname = "John Doe",
            uuidToken = "token-1",
            memberStatus = MemberStatus.ACTIVE
        ).apply { createdAt = LocalDateTime.of(2024, 12, 15, 0, 0) }
        val secondMember = Member(
            id = 2L,
            email = "jane.smith@example.com",
            nickname = "Jane Smith",
            uuidToken = "token-2",
            memberStatus = MemberStatus.ACTIVE
        ).apply { createdAt = LocalDateTime.of(2024, 12, 14, 0, 0) }
        val page = PageImpl(listOf(firstMember, secondMember), pageable, 15)

        every {
            memberDomainService.findWithCondition(
                pageable = pageable,
                memberStatus = MemberStatus.ACTIVE,
                nicknameKeyword = "John",
                emailKeyword = null,
                sortBy = MemberSortBy.CREATED_AT,
                sortDirection = Sort.Direction.DESC
            )
        } returns page
        every { memberDomainService.countByMemberStatus(null) } returns 15L
        every { memberDomainService.countByMemberStatus(MemberStatus.ACTIVE) } returns 11L
        every { memberDomainService.countByMemberStatus(MemberStatus.DELETED) } returns 4L

        // when
        val result = adminMemberService.searchMembers(
            pageable = pageable,
            statusFilter = MemberStatusFilter.ACTIVE,
            searchType = MemberListSearchType.NICKNAME,
            keyword = " John ",
            sortBy = MemberListSortBy.SUBSCRIBE_DATE,
            sortDirection = Sort.Direction.DESC
        )

        // then
        assertThat(result.pageNumber).isEqualTo(0)
        assertThat(result.totalCount).isEqualTo(15)
        assertThat(result.content).hasSize(2)
        assertThat(result.content[0].nickname).isEqualTo("John Doe")
        assertThat(result.statusCount.allCount).isEqualTo(15)
        assertThat(result.statusCount.activeCount).isEqualTo(11)
        assertThat(result.statusCount.deletedCount).isEqualTo(4)
        verify(exactly = 1) {
            memberDomainService.findWithCondition(
                pageable = pageable,
                memberStatus = MemberStatus.ACTIVE,
                nicknameKeyword = "John",
                emailKeyword = null,
                sortBy = MemberSortBy.CREATED_AT,
                sortDirection = Sort.Direction.DESC
            )
        }
    }

    @Test
    @DisplayName("정렬 조건이 EMAIL이면 도메인 정렬이 EMAIL로 전달된다")
    fun searchMembersWithEmailSortTest() {
        // given
        val pageable = PageRequest.of(1, 10)
        val member = Member(
            id = 3L,
            email = "alpha@example.com",
            nickname = "Alpha",
            uuidToken = "token-3",
            memberStatus = MemberStatus.ACTIVE
        ).apply { createdAt = LocalDateTime.of(2024, 12, 13, 0, 0) }
        val page = PageImpl(listOf(member), pageable, 21)

        every {
            memberDomainService.findWithCondition(
                pageable = pageable,
                memberStatus = null,
                nicknameKeyword = null,
                emailKeyword = "alpha",
                sortBy = MemberSortBy.EMAIL,
                sortDirection = Sort.Direction.ASC
            )
        } returns page
        every { memberDomainService.countByMemberStatus(null) } returns 21L
        every { memberDomainService.countByMemberStatus(MemberStatus.ACTIVE) } returns 18L
        every { memberDomainService.countByMemberStatus(MemberStatus.DELETED) } returns 3L

        // when
        val result = adminMemberService.searchMembers(
            pageable = pageable,
            statusFilter = MemberStatusFilter.ALL,
            searchType = MemberListSearchType.EMAIL,
            keyword = " alpha ",
            sortBy = MemberListSortBy.EMAIL,
            sortDirection = Sort.Direction.ASC
        )

        // then
        assertThat(result.totalCount).isEqualTo(21)
        assertThat(result.content[0].email).isEqualTo("alpha@example.com")
        verify(exactly = 1) {
            memberDomainService.findWithCondition(
                pageable = pageable,
                memberStatus = null,
                nicknameKeyword = null,
                emailKeyword = "alpha",
                sortBy = MemberSortBy.EMAIL,
                sortDirection = Sort.Direction.ASC
            )
        }
    }

    @Test
    @DisplayName("검색 타입이 없으면 닉네임과 이메일 조건을 함께 검색한다")
    fun searchMembersWithCombinedKeywordTest() {
        // given
        val pageable = PageRequest.of(0, 20)
        val page = PageImpl(emptyList<Member>(), pageable, 0)

        every {
            memberDomainService.findWithCondition(
                pageable = pageable,
                memberStatus = MemberStatus.DELETED,
                nicknameKeyword = "john",
                emailKeyword = "john",
                sortBy = MemberSortBy.STATUS,
                sortDirection = Sort.Direction.DESC
            )
        } returns page
        every { memberDomainService.countByMemberStatus(null) } returns 0L
        every { memberDomainService.countByMemberStatus(MemberStatus.ACTIVE) } returns 0L
        every { memberDomainService.countByMemberStatus(MemberStatus.DELETED) } returns 0L

        // when
        adminMemberService.searchMembers(
            pageable = pageable,
            statusFilter = MemberStatusFilter.DELETED,
            searchType = null,
            keyword = " john ",
            sortBy = MemberListSortBy.STATUS,
            sortDirection = Sort.Direction.DESC
        )

        // then
        verify(exactly = 1) {
            memberDomainService.findWithCondition(
                pageable = pageable,
                memberStatus = MemberStatus.DELETED,
                nicknameKeyword = "john",
                emailKeyword = "john",
                sortBy = MemberSortBy.STATUS,
                sortDirection = Sort.Direction.DESC
            )
        }
    }
}
