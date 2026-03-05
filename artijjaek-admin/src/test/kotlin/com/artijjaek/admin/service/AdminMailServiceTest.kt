package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostArticleMailRequest
import com.artijjaek.admin.dto.request.PostNewCompanyMailRequest
import com.artijjaek.admin.dto.request.PostNoticeMailRequest
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.*
import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.category.enums.PublishType
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.company.service.CompanyDomainService
import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.entity.EmailOutboxAttempt
import com.artijjaek.core.domain.mail.dto.DailyEmailSendAttemptCount
import com.artijjaek.core.domain.mail.enums.EmailOutboxAttemptResult
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxStatus
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.artijjaek.core.domain.mail.queue.publisher.MailQueuePublisher
import com.artijjaek.core.domain.mail.queue.trigger.MailDispatchTrigger
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.member.enums.MemberStatus
import com.artijjaek.core.domain.member.service.MemberDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

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
    lateinit var companyDomainService: CompanyDomainService

    @MockK
    lateinit var mailQueuePublisher: MailQueuePublisher

    @MockK
    lateinit var emailOutboxDomainService: EmailOutboxDomainService

    @MockK
    lateinit var mailDispatchTrigger: MailDispatchTrigger

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
        justRun { mailQueuePublisher.enqueueWelcomeMail(any(), any()) }

        // when
        adminMailService.sendWelcomeMail(request)

        // then
        verify(exactly = 1) { memberDomainService.findById(1L) }
        verify(exactly = 1) { memberDomainService.findById(2L) }
        verify(exactly = 2) { mailQueuePublisher.enqueueWelcomeMail(any(), any()) }
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
        verify(exactly = 0) { mailQueuePublisher.enqueueWelcomeMail(any(), any()) }
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
        verify(exactly = 0) { mailQueuePublisher.enqueueWelcomeMail(any(), any()) }
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

        every { articleDomainService.findAllByIdsWithCompany(listOf(101L, 102L)) } returns listOf(
            firstArticle,
            secondArticle
        )
        every { memberDomainService.findById(1L) } returns member
        justRun { mailQueuePublisher.enqueueArticleMail(any(), any(), any()) }

        // when
        adminMailService.sendArticleMail(request)

        // then
        verify(exactly = 1) { articleDomainService.findAllByIdsWithCompany(listOf(101L, 102L)) }
        verify(exactly = 1) { memberDomainService.findById(1L) }
        verify(exactly = 1) { mailQueuePublisher.enqueueArticleMail(any(), any(), any()) }
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
        verify(exactly = 0) { mailQueuePublisher.enqueueArticleMail(any(), any(), any()) }
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
        justRun { mailQueuePublisher.enqueueNoticeMail(any(), any(), any(), any()) }

        // when
        adminMailService.sendNoticeMail(request)

        // then
        verify(exactly = 1) { memberDomainService.findById(1L) }
        verify(exactly = 1) { memberDomainService.findById(2L) }
        verify(exactly = 2) {
            mailQueuePublisher.enqueueNoticeMail(
                any(),
                "신규 회사 추가 안내",
                "구독 가능한 회사가 추가되었습니다.",
                any()
            )
        }
    }

    @Test
    @DisplayName("특정 회원들에게 신규 회사 추가 안내 이메일을 발송한다")
    fun sendNewCompanyMailTest() {
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
        val companyA = Company(
            id = 10L,
            nameKr = "회사A",
            nameEn = "CompanyA",
            logo = "logo-a",
            baseUrl = "base-a",
            blogUrl = "blog-a",
            crawlUrl = "crawl-a",
            crawlAvailability = true
        )
        val companyB = Company(
            id = 11L,
            nameKr = "회사B",
            nameEn = "CompanyB",
            logo = "logo-b",
            baseUrl = "base-b",
            blogUrl = "blog-b",
            crawlUrl = "crawl-b",
            crawlAvailability = true
        )
        val request = PostNewCompanyMailRequest(
            memberIds = listOf(1L, 2L, 1L),
            companyIds = listOf(10L, 11L, 10L)
        )

        every { companyDomainService.findAllOrByIds(listOf(10L, 11L)) } returns listOf(companyA, companyB)
        every { memberDomainService.findById(1L) } returns firstMember
        every { memberDomainService.findById(2L) } returns secondMember
        justRun { mailQueuePublisher.enqueueNewCompanyMail(any(), any(), any()) }

        // when
        adminMailService.sendNewCompanyMail(request)

        // then
        verify(exactly = 1) { companyDomainService.findAllOrByIds(listOf(10L, 11L)) }
        verify(exactly = 1) { memberDomainService.findById(1L) }
        verify(exactly = 1) { memberDomainService.findById(2L) }
        verify(exactly = 2) { mailQueuePublisher.enqueueNewCompanyMail(any(), any(), any()) }
    }

    @Test
    @DisplayName("신규 회사 메일 발송 시 존재하지 않는 회사 ID가 포함되면 예외가 발생한다")
    fun sendNewCompanyMailWithNotFoundCompanyTest() {
        // given
        val request = PostNewCompanyMailRequest(
            memberIds = listOf(1L),
            companyIds = listOf(10L, 11L)
        )
        every { companyDomainService.findAllOrByIds(listOf(10L, 11L)) } returns emptyList()

        // when
        val exception = assertThrows<ApplicationException> {
            adminMailService.sendNewCompanyMail(request)
        }

        // then
        assertThat(exception.code).isEqualTo(COMPANY_NOT_FOUND_ERROR.code)
        assertThat(exception.message).isEqualTo(COMPANY_NOT_FOUND_ERROR.message)
        verify(exactly = 0) { memberDomainService.findById(any()) }
        verify(exactly = 0) { mailQueuePublisher.enqueueNewCompanyMail(any(), any(), any()) }
    }

    @Test
    @DisplayName("일자별 이메일 전송 성공 수를 조회할 때 빈 날짜는 0으로 채운다")
    fun getDailySentCountsTest() {
        // given
        every {
            emailOutboxDomainService.countDailySuccessAttempts(
                startDateTime = LocalDate.of(2026, 2, 1).atStartOfDay(),
                endDateTimeExclusive = LocalDate.of(2026, 2, 4).atStartOfDay(),
                requestedBy = EmailOutboxRequestedBy.ADMIN_API
            )
        } returns listOf(
            DailyEmailSendAttemptCount(date = LocalDate.of(2026, 2, 1), count = 5),
            DailyEmailSendAttemptCount(date = LocalDate.of(2026, 2, 3), count = 2)
        )

        // when
        val result = adminMailService.getDailySentCounts(
            startDate = LocalDate.of(2026, 2, 1),
            endDate = LocalDate.of(2026, 2, 3),
            requestedBy = EmailOutboxRequestedBy.ADMIN_API
        )

        // then
        assertThat(result).hasSize(3)
        assertThat(result[0].date).isEqualTo(LocalDate.of(2026, 2, 1))
        assertThat(result[0].sentCount).isEqualTo(5)
        assertThat(result[1].date).isEqualTo(LocalDate.of(2026, 2, 2))
        assertThat(result[1].sentCount).isEqualTo(0)
        assertThat(result[2].date).isEqualTo(LocalDate.of(2026, 2, 3))
        assertThat(result[2].sentCount).isEqualTo(2)
    }

    @Test
    @DisplayName("일자별 이메일 전송 실패 수를 조회할 때 빈 날짜는 0으로 채운다")
    fun getDailyFailedCountsTest() {
        // given
        every {
            emailOutboxDomainService.countDailyFailureAttempts(
                startDateTime = LocalDate.of(2026, 2, 1).atStartOfDay(),
                endDateTimeExclusive = LocalDate.of(2026, 2, 4).atStartOfDay(),
                requestedBy = null
            )
        } returns listOf(
            DailyEmailSendAttemptCount(date = LocalDate.of(2026, 2, 2), count = 4)
        )

        // when
        val result = adminMailService.getDailyFailedCounts(
            startDate = LocalDate.of(2026, 2, 1),
            endDate = LocalDate.of(2026, 2, 3),
            requestedBy = null
        )

        // then
        assertThat(result).hasSize(3)
        assertThat(result[0].failedCount).isEqualTo(0)
        assertThat(result[1].failedCount).isEqualTo(4)
        assertThat(result[2].failedCount).isEqualTo(0)
    }

    @Test
    @DisplayName("일자별 이메일 전송 성공 수 조회 시 시작일이 종료일보다 늦으면 예외가 발생한다")
    fun getDailySentCountsWithInvalidDateRangeTest() {
        val exception = assertThrows<ApplicationException> {
            adminMailService.getDailySentCounts(
                startDate = LocalDate.of(2026, 2, 3),
                endDate = LocalDate.of(2026, 2, 1),
                requestedBy = null
            )
        }

        assertThat(exception.code).isEqualTo(REQUEST_VALIDATION_ERROR.code)
    }

    @Test
    @DisplayName("이메일 전송 시도 이력을 시도 시점 내림차순으로 조회한다")
    fun searchOutboxAttemptsTest() {
        // given
        val pageable = PageRequest.of(0, 20)
        val firstOutbox = EmailOutbox(
            id = 101L,
            mailType = EmailOutboxType.NOTICE,
            recipientEmail = "a@test.com",
            subject = "A",
            payloadJson = "{}",
            status = EmailOutboxStatus.SENT,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            requestedAt = LocalDateTime.parse("2026-02-27T09:00:00")
        )
        val secondOutbox = EmailOutbox(
            id = 102L,
            mailType = EmailOutboxType.ARTICLE,
            recipientEmail = "b@test.com",
            subject = "B",
            payloadJson = "{}",
            status = EmailOutboxStatus.FAIL,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            requestedAt = LocalDateTime.parse("2026-02-27T10:00:00")
        )
        val firstAttempt = EmailOutboxAttempt(
            id = 10L,
            emailOutbox = firstOutbox,
            attemptNo = 1,
            result = EmailOutboxAttemptResult.SUCCESS,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            occurredAt = LocalDateTime.parse("2026-02-28T13:00:00")
        )
        val secondAttempt = EmailOutboxAttempt(
            id = 11L,
            emailOutbox = secondOutbox,
            attemptNo = 2,
            result = EmailOutboxAttemptResult.FAIL,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            occurredAt = LocalDateTime.parse("2026-02-28T12:00:00")
        )
        val page = PageImpl(listOf(firstAttempt, secondAttempt), pageable, 2)

        every {
            emailOutboxDomainService.searchAttempts(
                pageable = any(),
                status = EmailOutboxAttemptResult.FAIL,
                requestedBy = EmailOutboxRequestedBy.ADMIN_API,
                occurredAtFrom = LocalDate.of(2026, 2, 28).atStartOfDay(),
                occurredAtTo = LocalDate.of(2026, 3, 1).atStartOfDay()
            )
        } returns page

        // when
        val result = adminMailService.searchOutboxAttempts(
            pageable = pageable,
            status = EmailOutboxAttemptResult.FAIL,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            startDate = LocalDate.of(2026, 2, 28),
            endDate = LocalDate.of(2026, 2, 28)
        )

        // then
        assertThat(result.totalCount).isEqualTo(2)
        assertThat(result.content).hasSize(2)
        assertThat(result.content[0].occurredAt).isEqualTo(LocalDateTime.parse("2026-02-28T13:00:00"))
        assertThat(result.content[0].status).isEqualTo(EmailOutboxAttemptResult.SUCCESS)
    }

    @Test
    @DisplayName("이메일 전송 시도 이력 조회 시 시작일이 종료일보다 늦으면 예외가 발생한다")
    fun searchOutboxAttemptsWithInvalidDateRangeTest() {
        val exception = assertThrows<ApplicationException> {
            adminMailService.searchOutboxAttempts(
                pageable = PageRequest.of(0, 20),
                status = null,
                requestedBy = null,
                startDate = LocalDate.of(2026, 3, 1),
                endDate = LocalDate.of(2026, 2, 28)
            )
        }

        assertThat(exception.code).isEqualTo(REQUEST_VALIDATION_ERROR.code)
    }

    @Test
    @DisplayName("FAIL 상태 메일은 수동 재시도로 PENDING 전환 및 감사정보가 기록된다")
    fun retryOutboxFailStatusTest() {
        // given
        val outbox = EmailOutbox(
            id = 10L,
            mailType = EmailOutboxType.NOTICE,
            recipientEmail = "test@test.com",
            subject = "subject",
            payloadJson = "{}",
            status = EmailOutboxStatus.FAIL,
            attemptCount = 3,
            maxAttempts = 5,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            requestedAt = LocalDateTime.now()
        )
        every { emailOutboxDomainService.findById(10L) } returns outbox
        every { emailOutboxDomainService.save(any()) } answers { firstArg() }
        justRun { mailDispatchTrigger.dispatchOutbox(10L) }
        // when
        adminMailService.retryOutbox(10L, false, 99L)

        // then
        assertThat(outbox.status).isEqualTo(EmailOutboxStatus.PENDING)
        assertThat(outbox.manualRetryCount).isEqualTo(1)
        assertThat(outbox.lastRetriedByAdminId).isEqualTo(99L)
        assertThat(outbox.lastRetriedAt).isNotNull
        verify(exactly = 1) { emailOutboxDomainService.save(outbox) }
        verify(exactly = 1) { mailDispatchTrigger.dispatchOutbox(10L) }
    }

    @Test
    @DisplayName("SENT 상태 메일은 수동 재시도할 수 없다")
    fun retryOutboxNotAllowedStatusTest() {
        // given
        val outbox = EmailOutbox(
            id = 11L,
            mailType = EmailOutboxType.WELCOME,
            recipientEmail = "test@test.com",
            subject = "subject",
            payloadJson = "{}",
            status = EmailOutboxStatus.SENT,
            requestedBy = EmailOutboxRequestedBy.ADMIN_API,
            requestedAt = LocalDateTime.now()
        )
        every { emailOutboxDomainService.findById(11L) } returns outbox

        // when
        val exception = assertThrows<ApplicationException> {
            adminMailService.retryOutbox(11L, false, 1L)
        }

        // then
        assertThat(exception.code).isEqualTo(MAIL_OUTBOX_RETRY_NOT_ALLOWED_ERROR.code)
        verify(exactly = 0) { emailOutboxDomainService.save(any()) }
        verify(exactly = 0) { mailDispatchTrigger.dispatchOutbox(any()) }
    }

}
