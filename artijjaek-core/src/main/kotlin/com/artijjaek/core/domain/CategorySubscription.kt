package com.artijjaek.core.domain

import jakarta.persistence.*

@Entity
class CategorySubscription(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_subscription_id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    ) : BaseEntity() {

}