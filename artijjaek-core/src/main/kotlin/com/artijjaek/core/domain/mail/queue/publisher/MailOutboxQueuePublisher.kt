package com.artijjaek.core.domain.mail.queue.publisher

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.CompanyAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.domain.mail.dto.*
import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.artijjaek.core.domain.mail.queue.trigger.MailDispatchTrigger
import com.artijjaek.core.domain.mail.service.EmailOutboxDomainService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MailOutboxQueuePublisher(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val objectMapper: ObjectMapper,
    private val mailDispatchTrigger: MailDispatchTrigger,
) : MailQueuePublisher {

    override fun enqueueWelcomeMail(memberData: MemberAlertDto, requestedBy: EmailOutboxRequestedBy) {
        val payload = WelcomeMailPayload(member = MemberSnapshot.from(memberData))
        val subject = "[아티짹] 환영합니다 ${memberData.nickname}님!"

        saveOutbox(
            mailType = EmailOutboxType.WELCOME,
            recipientEmail = memberData.email!!,
            subject = subject,
            payload = payload,
            requestedBy = requestedBy
        )
    }

    override fun enqueueArticleMail(
        memberData: MemberAlertDto,
        articleDatas: List<ArticleAlertDto>,
        requestedBy: EmailOutboxRequestedBy,
    ) {
        val payload = ArticleMailPayload(
            member = MemberSnapshot.from(memberData),
            articles = articleDatas.map {
                ArticleSnapshot(
                    title = it.title,
                    link = it.link,
                    companyNameKr = it.companyNameKr,
                    companyLogo = it.companyLogo,
                    image = it.image
                )
            }
        )

        val subject = "[아티짹] ${LocalDate.now()} 아티클 목록"
        saveOutbox(
            mailType = EmailOutboxType.ARTICLE,
            recipientEmail = memberData.email!!,
            subject = subject,
            payload = payload,
            requestedBy = requestedBy
        )
    }

    override fun enqueueNoticeMail(
        memberData: MemberAlertDto,
        title: String,
        content: String,
        requestedBy: EmailOutboxRequestedBy,
    ) {
        val payload = NoticeMailPayload(
            member = MemberSnapshot.from(memberData),
            title = title.trim(),
            content = content.trim()
        )
        val subject = "[아티짹] ${title.trim()}"

        saveOutbox(
            mailType = EmailOutboxType.NOTICE,
            recipientEmail = memberData.email!!,
            subject = subject,
            payload = payload,
            requestedBy = requestedBy
        )
    }

    override fun enqueueNewCompanyMail(
        memberData: MemberAlertDto,
        companies: List<CompanyAlertDto>,
        requestedBy: EmailOutboxRequestedBy,
    ) {
        val payload = NewCompanyMailPayload(
            member = MemberSnapshot.from(memberData),
            companies = companies.map { CompanySnapshot.from(it) }
        )
        val subject = "[아티짹] 신규 구독 회사가 추가되었어요"

        saveOutbox(
            mailType = EmailOutboxType.NEW_COMPANY,
            recipientEmail = memberData.email!!,
            subject = subject,
            payload = payload,
            requestedBy = requestedBy
        )
    }

    private fun saveOutbox(
        mailType: EmailOutboxType,
        recipientEmail: String,
        subject: String,
        payload: Any,
        requestedBy: EmailOutboxRequestedBy,
    ) {
        val outbox = EmailOutbox(
            mailType = mailType,
            recipientEmail = recipientEmail.trim(),
            subject = subject,
            payloadJson = objectMapper.writeValueAsString(payload),
            requestedBy = requestedBy
        )
        val saved = emailOutboxDomainService.save(outbox)
        mailDispatchTrigger.dispatchOutbox(saved.id!!)
    }
}
