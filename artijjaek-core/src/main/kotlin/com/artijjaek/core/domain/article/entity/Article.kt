package com.artijjaek.core.domain.article.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.category.entity.Category
import com.artijjaek.core.domain.company.entity.Company
import jakarta.persistence.*

@Entity
class Article(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category?,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = true)
    var description: String?,

    @Column(columnDefinition = "TEXT", nullable = true)
    var image: String?,

    @Column(columnDefinition = "TEXT", nullable = false)
    var link: String,

    ) : BaseEntity() {

    fun changeCategory(category: Category?) {
        this.category = category
    }

}