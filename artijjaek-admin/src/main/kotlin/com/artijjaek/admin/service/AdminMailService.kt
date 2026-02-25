package com.artijjaek.admin.service

import com.artijjaek.admin.dto.request.PostArticleMailRequest
import com.artijjaek.admin.dto.request.PostWelcomeMailRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.*
import com.artijjaek.core.common.mail.dto.ArticleAlertDto
import com.artijjaek.core.common.mail.dto.MemberAlertDto
import com.artijjaek.core.common.mail.service.MailService
import com.artijjaek.core.domain.article.service.ArticleDomainService
import com.artijjaek.core.domain.member.service.MemberDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminMailService(
    private val memberDomainService: MemberDomainService,
    private val articleDomainService: ArticleDomainService,
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

    @Transactional(readOnly = true)
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

            mailService.sendArticleMail(MemberAlertDto.from(member), articleAlertDtos)
        }
    }
}
