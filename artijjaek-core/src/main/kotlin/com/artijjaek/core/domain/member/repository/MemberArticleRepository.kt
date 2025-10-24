package com.artijjaek.core.domain.member.repository

import com.artijjaek.core.domain.member.entity.MemberArticle
import org.springframework.data.jpa.repository.JpaRepository

interface MemberArticleRepository : JpaRepository<MemberArticle, Long> {
}