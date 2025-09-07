package com.noati.core.service

import com.noati.core.domain.MemberArticle
import com.noati.core.repository.MemberArticleRepository
import org.springframework.stereotype.Service

@Service
class MemberArticleDomainService(
    val memberArticleRepository: MemberArticleRepository,
) {

    fun save(memberArticle: MemberArticle) {
        memberArticleRepository.save(memberArticle)
    }

}