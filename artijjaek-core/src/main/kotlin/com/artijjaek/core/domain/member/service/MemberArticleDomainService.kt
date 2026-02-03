package com.artijjaek.core.domain.member.service

import com.artijjaek.core.domain.member.entity.MemberArticle
import com.artijjaek.core.domain.member.repository.MemberArticleRepository
import org.springframework.stereotype.Service

@Service
class MemberArticleDomainService(
    val memberArticleRepository: MemberArticleRepository,
) {

    fun save(memberArticle: MemberArticle) {
        memberArticleRepository.save(memberArticle)
    }

    fun saveAll(memberArticles: List<MemberArticle>) {
        memberArticleRepository.saveAll(memberArticles)
    }

}