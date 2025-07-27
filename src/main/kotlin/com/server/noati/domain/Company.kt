package com.server.noati.domain

import jakarta.persistence.*

@Entity
class Company(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var logo: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var blogUrl: String,

    ) : BaseEntity() {
    companion object {
        fun of(name: String, logo: String, blogUrl: String): Company {
            return Company(name = name, logo = logo, blogUrl = blogUrl)
        }
    }
}