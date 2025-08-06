package com.server.noati.domain

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