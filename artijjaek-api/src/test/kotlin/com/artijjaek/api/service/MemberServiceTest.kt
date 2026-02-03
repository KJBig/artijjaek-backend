package com.artijjaek.api.service

import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import com.artijjaek.core.common.mail.service.MailService
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.CategoryType
import com.artijjaek.core.domain.category.service.CategoryDomainService
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberDomainService
import com.artijjaek.core.domain.subscription.entity.CategorySubscription
import com.artijjaek.core.domain.subscription.entity.CompanySubscription
import com.artijjaek.core.domain.subscription.service.CategorySubscriptionDomainService
import com.artijjaek.core.domain.subscription.service.CompanySubscriptionDomainService
import com.artijjaek.core.domain.unsubscription.service.UnsubscriptionDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class MemberServiceTest {

    @InjectMockKs
    lateinit var memberService: MemberService

    @MockK
    lateinit var memberDomainService: MemberDomainService

    @MockK
    lateinit var companyDomainService: CompanyDomainService

    @MockK
    lateinit var companySubscriptionDomainService: CompanySubscriptionDomainService

    @MockK
    lateinit var categoryDomainService: CategoryDomainService

    @MockK
    lateinit var categorySubscriptionDomainService: CategorySubscriptionDomainService

    @MockK
    lateinit var unsubscriptionDomainService: UnsubscriptionDomainService

    @MockK
    lateinit var mailService: MailService

    @Test
    @DisplayName("이메일로 구독을 시작할 수 있다")
    fun registerTest() {
        // given
        val request = RegisterMemberRequest(
            email = "newuser@example.com",
            nickname = "securePass",
            categoryIds = mutableListOf(1),
            companyIds = mutableListOf(1)
        )

        val newMember = Member(
            email = request.email,
            nickname = request.nickname,
            uuidToken = "some-uuid-token",
            memberStatus = MemberStatus.ACTIVE,
        )

        val company = Company(
            nameKr = "회사1",
            nameEn = "Company1",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )
        val companies = mutableListOf(company)

        val category = Category(
            name = "카테고리1",
            categoryType = CategoryType.PUBLISH
        )
        val categories = mutableListOf(category)

        every { memberDomainService.findByEmailAndMemberStatus(any(), any()) }.returns(null)
        every { memberDomainService.save(any()) }.returns(newMember)
        every { companyDomainService.findByIdsOrAll(request.companyIds) }.returns(companies)
        justRun { companySubscriptionDomainService.saveAll(any()) }
        every { categoryDomainService.findByIdsOrAll(request.categoryIds) }.returns(categories)
        justRun { categorySubscriptionDomainService.saveAll(any()) }
        justRun { mailService.sendSubscribeMail(any()) }


        // when
        memberService.register(request)


        // then
        verify { memberDomainService.save(any()) }
        verify { companySubscriptionDomainService.saveAll(any()) }
        verify { categorySubscriptionDomainService.saveAll(any()) }
        verify { mailService.sendSubscribeMail(any()) }
    }

    @Test
    @DisplayName("이미 존재하는 회원이면 예외가 발생한다")
    fun registerTest_Duplicate() {
        // given
        val request = RegisterMemberRequest(
            email = "newuser@example.com",
            nickname = "nickname",
            categoryIds = mutableListOf(1),
            companyIds = mutableListOf(1)
        )

        val member = Member(
            email = request.email,
            nickname = request.nickname,
            uuidToken = "some-uuid-token",
            memberStatus = MemberStatus.ACTIVE
        )

        every { memberDomainService.findByEmailAndMemberStatus(any(), any()) }.returns(member)


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            memberService.register(request)
        }

        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_DUPLICATE_ERROR.code)
        verify(exactly = 0) { memberDomainService.save(any()) }
        verify(exactly = 0) { companySubscriptionDomainService.saveAll(any()) }
        verify(exactly = 0) { categorySubscriptionDomainService.saveAll(any()) }
        verify(exactly = 0) { mailService.sendSubscribeMail(any()) }
    }

    @Test
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보 조회")
    fun getMemberDataWithTokenTest() {
        // given
        val email = "newuser@example.com"
        val nickname = "nickname"
        val uuIdToken = "some-uuid-token"

        val member = Member(
            email = email,
            nickname = nickname,
            uuidToken = uuIdToken,
            memberStatus = MemberStatus.ACTIVE
        )

        val company = Company(
            id = 1L,
            nameKr = "회사1",
            nameEn = "Company1",
            logo = "http://example.com/logo1.png",
            baseUrl = "http://example.com",
            crawlUrl = "http://example.com/crawl1",
            crawlAvailability = true
        )
        val companySubscription = CompanySubscription(
            member = member,
            company = company
        )

        val category = Category(
            id = 1L,
            name = "카테고리1",
            categoryType = CategoryType.PUBLISH
        )
        val categorySubscription = CategorySubscription(
            member = member,
            category = category
        )

        every { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }.returns(member)
        every { companySubscriptionDomainService.findAllByMember(member) }
            .returns(mutableListOf(companySubscription))
        every { categorySubscriptionDomainService.findAllByMember(member) }
            .returns(mutableListOf(categorySubscription))


        // when
        val memberData = memberService.getMemberDataWithToken(email, uuIdToken)


        // then
        assertThat(memberData.email).isEqualTo(email)
        assertThat(memberData.nickname).isEqualTo(nickname)
        assertThat(memberData.companyIds.size).isEqualTo(1)
        assertThat(memberData.categoryIds.size).isEqualTo(1)
    }

    @Test
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보 조회 - 사용자 없음")
    fun getMemberDataWithTokenTest_MemberNotFound() {
        // given
        val email = "newuser@example.com"
        val uuIdToken = "some-uuid-token"

        every { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }.returns(null)


        // when
        val exception = assertThrows(ApplicationException::class.java) {
        }
        memberService.getMemberDataWithToken(email, uuIdToken)


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_NOT_FOUND_ERROR.code)
        verify(exactly = 0) { companySubscriptionDomainService.findAllByMember(any()) }
        verify(exactly = 0) { categorySubscriptionDomainService.findAllByMember(any()) }
    }

    @Test
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보 조회 - 사용자 토큰 불일치")
    fun getMemberDataWithTokenTest_MemberTokenNotMatch() {
        // given
        val email = "newuser@example.com"
        val nickname = "nickname"
        val uuIdToken = "some-uuid-token"
        val wrongToken = "wrong-uuid-token"

        val member = Member(
            email = email,
            nickname = nickname,
            uuidToken = uuIdToken,
            memberStatus = MemberStatus.ACTIVE
        )

        every { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }.returns(member)


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            memberService.getMemberDataWithToken(email, wrongToken)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_TOKEN_NOT_MATCH_ERROR.code)
        verify(exactly = 0) { companySubscriptionDomainService.findAllByMember(any()) }
        verify(exactly = 0) { categorySubscriptionDomainService.findAllByMember(any()) }
    }
}