package com.artijjaek.core.domain.mail.service

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.domain.mail.dto.*
import com.artijjaek.core.domain.mail.entity.EmailOutbox
import com.artijjaek.core.domain.mail.event.MailQueuedEvent
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.enums.EmailOutboxType
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class EmailOutboxEnqueueService(
    private val emailOutboxDomainService: EmailOutboxDomainService,
    private val objectMapper: ObjectMapper,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    fun enqueueWelcomeMail(memberData: MemberAlertDto, requestedBy: EmailOutboxRequestedBy) {
        val payload = WelcomeMailPayload(member = memberSnapshot(memberData))
        val subject = "[아티짹] 환영합니다 ${memberData.nickname}님!"

        saveOutbox(
            mailType = EmailOutboxType.WELCOME,
            recipientEmail = memberData.email!!,
            subject = subject,
            payload = payload,
            requestedBy = requestedBy
        )
    }

    fun enqueueArticleMail(
        memberData: MemberAlertDto,
        articleDatas: List<ArticleAlertDto>,
        requestedBy: EmailOutboxRequestedBy,
    ) {
        val payload = ArticleMailPayload(
            member = memberSnapshot(memberData),
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

    fun enqueueNoticeMail(
        memberData: MemberAlertDto,
        title: String,
        content: String,
        requestedBy: EmailOutboxRequestedBy,
    ) {
        val payload = NoticeMailPayload(
            member = memberSnapshot(memberData),
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
        applicationEventPublisher.publishEvent(MailQueuedEvent(saved.id!!))
    }

    private fun memberSnapshot(memberData: MemberAlertDto): MemberSnapshot {
        return MemberSnapshot(
            email = memberData.email!!,
            nickname = memberData.nickname,
            uuidToken = memberData.uuidToken
        )
    }
}
