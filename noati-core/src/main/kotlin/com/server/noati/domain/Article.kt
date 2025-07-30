package com.server.noati.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Article(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var postedDate: LocalDateTime,

    @Column(columnDefinition = "TEXT", nullable = false)
    var articleUrl: String,

    ) : BaseEntity() {
    companion object {
        fun of(company: Company, title: String, postedDate: LocalDateTime, articleUrl: String): Article {
            return Article(company = company, title = title, postedDate = postedDate, articleUrl = articleUrl)
        }
    }
}