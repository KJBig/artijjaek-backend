package com.artiting.core.domain

import jakarta.persistence.*

@Entity
class Company(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    var id: Long? = null,

    @Column(nullable = false)
    var nameKr: String,

    @Column(nullable = false)
    var nameEn: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var logo: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var baseUrl: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var crawlUrl: String,

    @Column(nullable = false)
    var crawlAvailability: Boolean,

    ) : BaseEntity() {

    fun chaneCrawlAvailability(crawlAvailability: Boolean) {
        this.crawlAvailability = crawlAvailability
    }

}