package com.artijjaek.core.repository

import com.artijjaek.core.domain.MemberArticle
import org.springframework.data.jpa.repository.JpaRepository

interface MemberArticleRepository : JpaRepository<MemberArticle, Long> {
}