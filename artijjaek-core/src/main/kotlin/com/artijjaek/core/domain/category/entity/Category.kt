package com.artijjaek.core.domain.category.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.category.enums.CategoryType
import jakarta.persistence.*

@Entity
class Category(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var categoryType: CategoryType,

    ) : BaseEntity() {

}