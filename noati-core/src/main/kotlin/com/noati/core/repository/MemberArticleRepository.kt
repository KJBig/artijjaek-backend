package com.noati.core.repository

import com.noati.core.domain.MemberArticle
import org.springframework.data.jpa.repository.JpaRepository

interface MemberArticleRepository : JpaRepository<MemberArticle, Long> {
}