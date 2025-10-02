package com.artijjaek.core.service

import com.artijjaek.core.domain.MemberArticle
import com.artijjaek.core.repository.MemberArticleRepository
import org.springframework.stereotype.Service

@Service
class MemberArticleDomainService(
    val memberArticleRepository: MemberArticleRepository,
) {

    fun save(memberArticle: MemberArticle) {
        memberArticleRepository.save(memberArticle)
    }

}