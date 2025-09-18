package com.artiting.core.service

import com.artiting.core.domain.MemberArticle
import com.artiting.core.repository.MemberArticleRepository
import org.springframework.stereotype.Service

@Service
class MemberArticleDomainService(
    val memberArticleRepository: MemberArticleRepository,
) {

    fun save(memberArticle: MemberArticle) {
        memberArticleRepository.save(memberArticle)
    }

}