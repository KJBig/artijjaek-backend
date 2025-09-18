package com.artiting.core.repository

import com.artiting.core.domain.MemberArticle
import org.springframework.data.jpa.repository.JpaRepository

interface MemberArticleRepository : JpaRepository<MemberArticle, Long> {
}