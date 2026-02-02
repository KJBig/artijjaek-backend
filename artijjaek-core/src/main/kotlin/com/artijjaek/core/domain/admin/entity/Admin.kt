package com.artijjaek.core.domain.admin.entity

import com.artijjaek.core.common.entity.BaseEntity
import com.artijjaek.core.domain.admin.enums.AdminRole
import jakarta.persistence.*

@Entity
class Admin(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var email: String,

    @Column(nullable = true)
    var password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    var adminRole: AdminRole,

    @Column(nullable = true)
    var refreshToken: String?,

    ) : BaseEntity() {

    fun changeRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }

}