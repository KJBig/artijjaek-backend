package com.artiting.core.domain

import com.artiting.core.enums.CategoryType
import jakarta.persistence.*

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
    @Column(nullable = true)
    var category: CategoryType?,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var description: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var image: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var link: String,

    ) : BaseEntity() {

    fun changeCategory(category: CategoryType?) {
        this.category = category
    }

}