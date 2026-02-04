package com.artijjaek.api.service

import com.artijjaek.api.dto.request.RegisterMemberRequest
import com.artijjaek.api.dto.request.SubscriptionChangeRequest
import com.artijjaek.api.dto.request.UnsubscriptionRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode
import com.artijjaek.core.common.mail.service.MailService
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
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
import com.artijjaek.core.domain.unsubscription.entity.Unsubscription
import com.artijjaek.core.domain.unsubscription.enums.UnSubscriptionReason
import com.artijjaek.core.domain.unsubscription.service.UnsubscriptionDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
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
            publishType = PublishType.PUBLISH
        )
        val categories = mutableListOf(category)

        every { memberDomainService.findByEmailAndMemberStatus(any(), any()) }.returns(null)
        every { memberDomainService.save(any()) }.returns(newMember)
        every { companyDomainService.findAllOrByIds(request.companyIds) }.returns(companies)
        justRun { companySubscriptionDomainService.saveAll(any()) }
        every { categoryDomainService.findAllOrByIds(request.categoryIds) }.returns(categories)
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
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보 조회 할 수 있다")
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
            publishType = PublishType.PUBLISH
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
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보 조회 할 수 있다 - 사용자 없음")
    fun getMemberDataWithTokenTest_MemberNotFound() {
        // given
        val email = "newuser@example.com"
        val uuIdToken = "some-uuid-token"

        every { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }.returns(null)


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            memberService.getMemberDataWithToken(email, uuIdToken)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_NOT_FOUND_ERROR.code)
        verify(exactly = 0) { companySubscriptionDomainService.findAllByMember(any<Member>()) }
        verify(exactly = 0) { categorySubscriptionDomainService.findAllByMember(any<Member>()) }
    }

    @Test
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보 조회 할 수 있다 - 사용자 토큰 불일치")
    fun getMemberDataWithTokenTest_MemberTokenNotMatch() {
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

        every { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }.returns(member)


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            memberService.getMemberDataWithToken(email, "wrong-uuid-token")
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_TOKEN_NOT_MATCH_ERROR.code)
        verify(exactly = 0) { companySubscriptionDomainService.findAllByMember(any<Member>()) }
        verify(exactly = 0) { categorySubscriptionDomainService.findAllByMember(any<Member>()) }
    }

    @Test
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보를 수정 할 수 있다")
    fun changeSubscriptionTest() {
        // given
        val email = "newuser@example.com"
        val nickname = "nickname"
        val uuIdToken = "some-uuid-token"

        val request = SubscriptionChangeRequest(
            email = email,
            token = uuIdToken,
            nickname = nickname,
            categoryIds = mutableListOf(1),
            companyIds = mutableListOf(1),
        )

        val member = Member(
            id = 1L,
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
            publishType = PublishType.PUBLISH
        )
        val categorySubscription = CategorySubscription(
            member = member,
            category = category
        )

        every { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }.returns(member)

        justRun { companySubscriptionDomainService.deleteAllByMemberId(member.id!!) }
        every { companySubscriptionDomainService.findAllByMember(member) }
            .returns(mutableListOf(companySubscription))

        justRun { categorySubscriptionDomainService.deleteAllByMemberId(member.id!!) }
        every { categorySubscriptionDomainService.findAllByMember(member) }
            .returns(mutableListOf(categorySubscription))


        // when
        memberService.changeSubscription(request)


        // then
        verify { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }
        verify { companySubscriptionDomainService.deleteAllByMemberId(any<Long>()) }
        verify { companySubscriptionDomainService.findAllByMember(any<Member>()) }
        verify { categorySubscriptionDomainService.deleteAllByMemberId(any<Long>()) }
        verify { categorySubscriptionDomainService.findAllByMember(any<Member>()) }
    }

    @Test
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보를 수정 할 수 있다 - 사용자 없음")
    fun changeSubscriptionTest_MemberNotFound() {
        // given
        val email = "newuser@example.com"
        val uuIdToken = "some-uuid-token"
        val nickname = "nickname"

        val request = SubscriptionChangeRequest(
            email = email,
            token = uuIdToken,
            nickname = nickname,
            categoryIds = mutableListOf(1),
            companyIds = mutableListOf(1),
        )

        every { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }.returns(null)


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            memberService.changeSubscription(request)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_NOT_FOUND_ERROR.code)
        verify(exactly = 0) { companySubscriptionDomainService.deleteAllByMemberId(any<Long>()) }
        verify(exactly = 0) { companySubscriptionDomainService.findAllByMember(any<Member>()) }
        verify(exactly = 0) { categorySubscriptionDomainService.deleteAllByMemberId(any<Long>()) }
        verify(exactly = 0) { categorySubscriptionDomainService.findAllByMember(any<Member>()) }
    }

    @Test
    @DisplayName("구독자 이메일과 토큰을 통해 구독정보를 수정 할 수 있다 - 사용자 토큰 불일치")
    fun changeSubscriptionTest_MemberTokenNotMatch() {
        // given
        val email = "newuser@example.com"
        val nickname = "nickname"
        val uuIdToken = "some-uuid-token"

        val request = SubscriptionChangeRequest(
            email = email,
            token = "wrong-uuid-token",
            nickname = nickname,
            categoryIds = mutableListOf(1),
            companyIds = mutableListOf(1),
        )

        val member = Member(
            email = email,
            nickname = nickname,
            uuidToken = uuIdToken,
            memberStatus = MemberStatus.ACTIVE
        )

        every { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }.returns(member)


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            memberService.changeSubscription(request)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_TOKEN_NOT_MATCH_ERROR.code)
        verify(exactly = 0) { companySubscriptionDomainService.findAllByMember(any<Member>()) }
        verify(exactly = 0) { categorySubscriptionDomainService.findAllByMember(any<Member>()) }
    }

    @Test
    @DisplayName("이메일과 토큰으로 구독을 해지 할 수 있다")
    fun cancelSubscriptionTest() {
        // given
        val email = "test@example.com"
        val nickname = "nickname"
        val uuIdToken = "some-uuid-token"

        val request = UnsubscriptionRequest(
            email = email,
            token = uuIdToken,
            reason = UnSubscriptionReason.NO_COMPANY,
            detail = "reason detail"
        )

        val member = Member(
            id = 1L,
            email = email,
            nickname = nickname,
            uuidToken = uuIdToken,
            memberStatus = MemberStatus.ACTIVE
        )

        every { memberDomainService.findByEmailAndMemberStatus(any(), any()) }.returns(member)
        every { unsubscriptionDomainService.saveUnsubscription(any()) }.returns(mockk())


        // when
        memberService.cancelSubscription(request)


        // then
        verify { memberDomainService.findByEmailAndMemberStatus(email, MemberStatus.ACTIVE) }
        verify { unsubscriptionDomainService.saveUnsubscription(any<Unsubscription>()) }
    }

    @Test
    @DisplayName("이메일과 토큰으로 구독을 해지 할 수 있다 - 사용자 없음")
    fun cancelSubscription_MemberNotFound() {
        // given
        val email = "test@example.com"
        val uuIdToken = "some-uuid-token"

        val request = UnsubscriptionRequest(
            email = email,
            token = uuIdToken,
            reason = UnSubscriptionReason.NO_COMPANY,
            detail = "reason detail"
        )

        every { memberDomainService.findByEmailAndMemberStatus(any(), any()) }.returns(null)


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            memberService.cancelSubscription(request)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_NOT_FOUND_ERROR.code)
        verify(exactly = 0) { unsubscriptionDomainService.saveUnsubscription(any()) }
    }

    @Test
    @DisplayName("이메일과 토큰으로 구독을 해지 할 수 있다 - 사용자 토큰 불일치")
    fun cancelSubscription_MemberTokenNotMatch() {
        // given
        val email = "test@example.com"
        val nickname = "nickname"
        val uuIdToken = "some-uuid-token"

        val request = UnsubscriptionRequest(
            email = email,
            token = "wrong-uuid-token",
            reason = UnSubscriptionReason.NO_COMPANY,
            detail = "reason detail"
        )

        val member = Member(
            id = 1L,
            email = email,
            nickname = nickname,
            uuidToken = uuIdToken,
            memberStatus = MemberStatus.ACTIVE
        )

        every { memberDomainService.findByEmailAndMemberStatus(any(), any()) }.returns(member)
        every { unsubscriptionDomainService.saveUnsubscription(any()) }.returns(mockk())


        // when
        val exception = assertThrows(ApplicationException::class.java) {
            memberService.cancelSubscription(request)
        }


        // then
        assertThat(exception.code).isEqualTo(ErrorCode.MEMBER_TOKEN_NOT_MATCH_ERROR.code)
        verify(exactly = 0) { unsubscriptionDomainService.saveUnsubscription(any()) }
    }

}