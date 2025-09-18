package com.artiting.core.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Email

@Entity
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var id: Long? = null,

    @Email
    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var nickname: String,

    ) : BaseEntity() {

}