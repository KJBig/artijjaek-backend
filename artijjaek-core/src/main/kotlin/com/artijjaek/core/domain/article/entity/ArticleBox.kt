package com.artijjaek.core.domain.article.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.member.entity.Member
import jakarta.persistence.*

@Entity
class ArticleBox(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_box_id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    var article: Article,

    ) : BaseEntity() {

}