package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.MEMBER_EMAIL_NOT_FOUND_ERROR
import com.artijjaek.core.common.error.ErrorCode.MEMBER_NOT_FOUND_ERROR
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.common.mail.service.MailService
import com.artijjaek.core.domain.member.service.MemberDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminMailService(
    private val memberDomainService: MemberDomainService,
    private val mailService: MailService,
) {

    @Transactional(readOnly = true)
    fun sendWelcomeMail(request: PostWelcomeMailRequest) {
        request.memberIds.distinct().forEach { memberId ->
            val member = memberDomainService.findById(memberId)
                ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

            if (member.email.isNullOrBlank()) {
                throw ApplicationException(MEMBER_EMAIL_NOT_FOUND_ERROR)
            }

            mailService.sendSubscribeMail(MemberAlertDto.from(member))
        }
    }
}
