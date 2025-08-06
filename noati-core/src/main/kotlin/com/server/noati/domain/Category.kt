package com.server.noati.domain

import com.server.noati.enums.CategoryType
import jakarta.persistence.*

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var categoryType: CategoryType,

    ) : BaseEntity() {


}