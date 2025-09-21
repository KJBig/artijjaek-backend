package com.artiting.core.domain

import com.artiting.core.enums.MemberStatus
import jakarta.persistence.*

@Entity
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var id: Long? = null,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var nickname: String,

    @Column(nullable = true)
    var uuidToken: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var memberStatus: MemberStatus,

    ) : BaseEntity() {

}