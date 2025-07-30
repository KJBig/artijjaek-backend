package com.server.noati.domain

import jakarta.persistence.*

@Entity
class Subscribe(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company,

    ) : BaseEntity() {
    companion object {
        fun of(member: Member, company: Company): Subscribe {
            return Subscribe(member = member, company = company)
        }
    }
}