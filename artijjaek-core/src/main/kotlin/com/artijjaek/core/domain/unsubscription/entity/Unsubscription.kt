package com.artijjaek.core.domain.unsubscription.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.member.entity.Member
import com.artijjaek.core.domain.unsubscription.enums.UnSubscriptionReason
import jakarta.persistence.*

@Entity
class Unsubscription(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unsubscription_id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var reason: UnSubscriptionReason,

    var detail: String,

    ) : BaseEntity() {

}