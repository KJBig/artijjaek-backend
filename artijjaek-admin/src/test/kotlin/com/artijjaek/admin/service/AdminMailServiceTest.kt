package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.MEMBER_EMAIL_NOT_FOUND_ERROR
import com.artijjaek.core.common.error.ErrorCode.MEMBER_NOT_FOUND_ERROR
import com.artijjaek.core.common.mail.service.MailService
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
    lateinit var mailService: MailService

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
        justRun { mailService.sendSubscribeMail(any()) }

        // when
        adminMailService.sendWelcomeMail(request)

        // then
        verify(exactly = 1) { memberDomainService.findById(1L) }
        verify(exactly = 1) { memberDomainService.findById(2L) }
        verify(exactly = 2) { mailService.sendSubscribeMail(any()) }
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
        verify(exactly = 0) { mailService.sendSubscribeMail(any()) }
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
        verify(exactly = 0) { mailService.sendSubscribeMail(any()) }
    }
}
