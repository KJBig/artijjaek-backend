package com.noati.core.domain

import com.noati.core.enums.CategoryType
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: CategoryType?,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var summery: String,

    @Column(nullable = false)
    var postedDate: LocalDate,

    @Column(columnDefinition = "TEXT", nullable = false)
    var articleUrl: String,

    ) : BaseEntity() {

    fun changeCategory(category: CategoryType?) {
        this.category = category
    }

}