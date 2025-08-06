package com.server.noati.domain

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

    ) : BaseEntity() {

}