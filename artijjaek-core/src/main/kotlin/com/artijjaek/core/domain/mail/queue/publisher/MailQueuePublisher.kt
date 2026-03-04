package com.artijjaek.core.domain.mail.queue.publisher

import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy

interface MailQueuePublisher {
    fun enqueueWelcomeMail(memberData: MemberAlertDto, requestedBy: EmailOutboxRequestedBy)
    fun enqueueArticleMail(
        memberData: MemberAlertDto,
        articleDatas: List<ArticleAlertDto>,
        requestedBy: EmailOutboxRequestedBy
    )

    fun enqueueNoticeMail(
        memberData: MemberAlertDto,
        title: String,
        content: String,
        requestedBy: EmailOutboxRequestedBy
    )
}
