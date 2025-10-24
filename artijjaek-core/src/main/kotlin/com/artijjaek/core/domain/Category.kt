package com.artijjaek.core.domain

import jakarta.persistence.*

@Entity
class Category(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    ) : BaseEntity() {

}