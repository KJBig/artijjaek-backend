package com.artijjaek.core.domain

import jakarta.persistence.*

@Entity
class CompanySubscription(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_subscription_id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company,

    ) : BaseEntity() {

}