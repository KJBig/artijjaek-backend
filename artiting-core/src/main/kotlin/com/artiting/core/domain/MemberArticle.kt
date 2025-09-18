package com.artiting.core.domain

import jakarta.persistence.*

@Entity
class MemberArticle(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_article_id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    var article: Article,

    ) : BaseEntity() {

}