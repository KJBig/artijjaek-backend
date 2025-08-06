package com.server.noati.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Article(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var summery: String,

    @Column(nullable = false)
    var postedDate: LocalDate,

    @Column(columnDefinition = "TEXT", nullable = false)
    var articleUrl: String,

    ) : BaseEntity() {

}