package com.artijjaek.core.domain.subscription.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.company.entity.Company
import com.artijjaek.core.domain.member.entity.Member
import jakarta.persistence.*

@Entity
class CompanySubscription(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_subscription_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company,

    ) : BaseEntity() {

}