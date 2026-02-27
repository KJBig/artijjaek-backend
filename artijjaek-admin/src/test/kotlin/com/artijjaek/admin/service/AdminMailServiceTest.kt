package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostArticleMailRequest
import com.artijjaek.admin.dto.request.PostNoticeMailRequest
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.ARTICLE_NOT_FOUND_ERROR
import com.artijjaek.core.common.error.ErrorCode.MEMBER_EMAIL_NOT_FOUND_ERROR
import com.artijjaek.core.common.error.ErrorCode.MEMBER_NOT_FOUND_ERROR
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.core.domain.mail.service.EmailOutboxEnqueueService
import com.artijjaek.core.domain.mail.service.EmailOutboxWorkerCoordinator
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AdminMailServiceTest {

    @InjectMockKs
    lateinit var adminMailService: AdminMailService

    @MockK
    lateinit var memberDomainService: MemberDomainService

    @MockK
    lateinit var articleDomainService: ArticleDomainService

    @MockK
    lateinit var emailOutboxEnqueueService: EmailOutboxEnqueueService

    @MockK
    lateinit var emailOutboxDomainService: EmailOutboxDomainService

    @MockK
    lateinit var emailOutboxWorkerCoordinator: EmailOutboxWorkerCoordinator

    @Test
    @DisplayName("특정 회원들에게 환영 이메일을 발송한다")
    fun sendWelcomeMailTest() {
        // given
        val firstMember = Member(
            id = 1L,
            email = "first@test.com",
            nickname = "first",
            uuidToken = "token-1",
            memberStatus = MemberStatus.ACTIVE
        )
        val secondMember = Member(
            id = 2L,
            email = "second@test.com",
            nickname = "second",
            uuidToken = "token-2",
            memberStatus = MemberStatus.ACTIVE
        )
        val request = PostWelcomeMailRequest(memberIds = listOf(1L, 2L, 1L))

        every { memberDomainService.findById(1L) } returns firstMember
        every { memberDomainService.findById(2L) } returns secondMember
        justRun { emailOutboxEnqueueService.enqueueWelcomeMail(any(), any()) }

        // when
        adminMailService.sendWelcomeMail(request)

        // then
        verify(exactly = 1) { memberDomainService.findById(1L) }
        verify(exactly = 1) { memberDomainService.findById(2L) }
        verify(exactly = 2) { emailOutboxEnqueueService.enqueueWelcomeMail(any(), any()) }
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID가 포함되면 예외가 발생한다")
    fun sendWelcomeMailWithNotFoundMemberTest() {
        // given
        val request = PostWelcomeMailRequest(memberIds = listOf(1L))
        every { memberDomainService.findById(1L) } returns null

        // when
        val exception = assertThrows<ApplicationException> {
            adminMailService.sendWelcomeMail(request)
        }

        // then
        assertThat(exception.code).isEqualTo(MEMBER_NOT_FOUND_ERROR.code)
        assertThat(exception.message).isEqualTo(MEMBER_NOT_FOUND_ERROR.message)
        verify(exactly = 0) { emailOutboxEnqueueService.enqueueWelcomeMail(any(), any()) }
    }

    @Test
    @DisplayName("회원 이메일이 없으면 예외가 발생한다")
    fun sendWelcomeMailWithMemberWithoutEmailTest() {
        // given
        val member = Member(
            id = 1L,
            email = null,
            nickname = "first",
            uuidToken = "token-1",
            memberStatus = MemberStatus.ACTIVE
        )
        val request = PostWelcomeMailRequest(memberIds = listOf(1L))
        every { memberDomainService.findById(1L) } returns member

        // when
        val exception = assertThrows<ApplicationException> {
            adminMailService.sendWelcomeMail(request)
        }

        // then
        assertThat(exception.code).isEqualTo(MEMBER_EMAIL_NOT_FOUND_ERROR.code)
        assertThat(exception.message).isEqualTo(MEMBER_EMAIL_NOT_FOUND_ERROR.message)
        verify(exactly = 0) { emailOutboxEnqueueService.enqueueWelcomeMail(any(), any()) }
    }

    @Test
    @DisplayName("특정 회원들에게 특정 아티클 목록 이메일을 발송한다")
    fun sendArticleMailTest() {
        // given
        val company = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "company-a",
            logo = "logo-a",
            baseUrl = "base-a",
            blogUrl = "blog-a",
            crawlUrl = "crawl-a",
            crawlAvailability = true
        )
        val category = Category(
            id = 20L,
            name = "백엔드",
            publishType = PublishType.PUBLISH
        )
        val firstArticle = Article(
            id = 101L,
            company = company,
            category = category,
            title = "첫 번째 아티클",
            description = "desc-1",
            image = "img-1",
            link = "link-1"
        )
        val secondArticle = Article(
            id = 102L,
            company = company,
            category = category,
            title = "두 번째 아티클",
            description = "desc-2",
            image = "img-2",
            link = "link-2"
        )
        val member = Member(
            id = 1L,
            email = "first@test.com",
            nickname = "first",
            uuidToken = "token-1",
            memberStatus = MemberStatus.ACTIVE
        )
        val request = PostArticleMailRequest(
            memberIds = listOf(1L, 1L),
            articleIds = listOf(101L, 102L, 101L)
        )

        every { articleDomainService.findAllByIdsWithCompany(listOf(101L, 102L)) } returns listOf(firstArticle, secondArticle)
        every { memberDomainService.findById(1L) } returns member
        justRun { emailOutboxEnqueueService.enqueueArticleMail(any(), any(), any()) }

        // when
        adminMailService.sendArticleMail(request)

        // then
        verify(exactly = 1) { articleDomainService.findAllByIdsWithCompany(listOf(101L, 102L)) }
        verify(exactly = 1) { memberDomainService.findById(1L) }
        verify(exactly = 1) { emailOutboxEnqueueService.enqueueArticleMail(any(), any(), any()) }
    }

    @Test
    @DisplayName("존재하지 않는 아티클 ID가 포함되면 예외가 발생한다")
    fun sendArticleMailWithNotFoundArticleTest() {
        // given
        val request = PostArticleMailRequest(
            memberIds = listOf(1L),
            articleIds = listOf(101L)
        )
        every { articleDomainService.findAllByIdsWithCompany(listOf(101L)) } returns emptyList()

        // when
        val exception = assertThrows<ApplicationException> {
            adminMailService.sendArticleMail(request)
        }

        // then
        assertThat(exception.code).isEqualTo(ARTICLE_NOT_FOUND_ERROR.code)
        assertThat(exception.message).isEqualTo(ARTICLE_NOT_FOUND_ERROR.message)
        verify(exactly = 0) { memberDomainService.findById(any()) }
        verify(exactly = 0) { emailOutboxEnqueueService.enqueueArticleMail(any(), any(), any()) }
    }

    @Test
    @DisplayName("특정 회원들에게 공지사항 이메일을 발송한다")
    fun sendNoticeMailTest() {
        // given
        val firstMember = Member(
            id = 1L,
            email = "first@test.com",
            nickname = "first",
            uuidToken = "token-1",
            memberStatus = MemberStatus.ACTIVE
        )
        val secondMember = Member(
            id = 2L,
            email = "second@test.com",
            nickname = "second",
            uuidToken = "token-2",
            memberStatus = MemberStatus.ACTIVE
        )
        val request = PostNoticeMailRequest(
            memberIds = listOf(1L, 2L, 1L),
            title = "신규 회사 추가 안내",
            content = "구독 가능한 회사가 추가되었습니다."
        )
        every { memberDomainService.findById(1L) } returns firstMember
        every { memberDomainService.findById(2L) } returns secondMember
        justRun { emailOutboxEnqueueService.enqueueNoticeMail(any(), any(), any(), any()) }

        // when
        adminMailService.sendNoticeMail(request)

        // then
        verify(exactly = 1) { memberDomainService.findById(1L) }
        verify(exactly = 1) { memberDomainService.findById(2L) }
        verify(exactly = 2) {
            emailOutboxEnqueueService.enqueueNoticeMail(
                any(),
                "신규 회사 추가 안내",
                "구독 가능한 회사가 추가되었습니다.",
                any()
            )
        }
    }

}
