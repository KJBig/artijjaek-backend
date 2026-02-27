package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostArticleMailRequest
import com.artijjaek.admin.dto.request.PostNoticeMailRequest
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.*
import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.domain.mail.enums.EmailOutboxRequestedBy
import com.artijjaek.core.domain.mail.service.EmailOutboxEnqueueService
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.member.service.MemberDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminMailService(
    private val memberDomainService: MemberDomainService,
    private val articleDomainService: ArticleDomainService,
    private val emailOutboxEnqueueService: EmailOutboxEnqueueService,
) {

    @Transactional
    fun sendWelcomeMail(request: PostWelcomeMailRequest) {
        request.memberIds.distinct().forEach { memberId ->
            val member = memberDomainService.findById(memberId)
                ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

            if (member.email.isNullOrBlank()) {
                throw ApplicationException(MEMBER_EMAIL_NOT_FOUND_ERROR)
            }

            emailOutboxEnqueueService.enqueueWelcomeMail(MemberAlertDto.from(member), EmailOutboxRequestedBy.ADMIN_API)
        }
    }

    @Transactional
    fun sendArticleMail(request: PostArticleMailRequest) {
        val articleIds = request.articleIds.distinct()
        val articles = articleDomainService.findAllByIdsWithCompany(articleIds)

        if (articles.size != articleIds.size) {
            throw ApplicationException(ARTICLE_NOT_FOUND_ERROR)
        }

        val articleAlertDtos = articles
            .sortedByDescending { it.createdAt }
            .map { ArticleAlertDto.from(it) }

        request.memberIds.distinct().forEach { memberId ->
            val member = memberDomainService.findById(memberId)
                ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

            if (member.email.isNullOrBlank()) {
                throw ApplicationException(MEMBER_EMAIL_NOT_FOUND_ERROR)
            }

            emailOutboxEnqueueService.enqueueArticleMail(
                memberData = MemberAlertDto.from(member),
                articleDatas = articleAlertDtos,
                requestedBy = EmailOutboxRequestedBy.ADMIN_API
            )
        }
    }

    @Transactional
    fun sendNoticeMail(request: PostNoticeMailRequest) {
        val title = request.title.trim()
        val content = request.content.trim()

        request.memberIds.distinct().forEach { memberId ->
            val member = memberDomainService.findById(memberId)
                ?: throw ApplicationException(MEMBER_NOT_FOUND_ERROR)

            if (member.email.isNullOrBlank()) {
                throw ApplicationException(MEMBER_EMAIL_NOT_FOUND_ERROR)
            }

            emailOutboxEnqueueService.enqueueNoticeMail(
                memberData = MemberAlertDto.from(member),
                title = title,
                content = content,
                requestedBy = EmailOutboxRequestedBy.ADMIN_API
            )
        }
    }
}
