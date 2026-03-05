package com.artijjaek.core.domain.company.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.company.enums.CrawlOrder
import com.artijjaek.core.domain.company.enums.CrawlPattern
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
    var blogUrl: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var crawlUrl: String,

    @Column(nullable = false)
    var crawlAvailability: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var crawlPattern: CrawlPattern = CrawlPattern.RSS,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var crawlOrder: CrawlOrder = CrawlOrder.NORMAL,

    ) : BaseEntity() {

    fun chaneCrawlAvailability(crawlAvailability: Boolean) {
        this.crawlAvailability = crawlAvailability
    }

}
