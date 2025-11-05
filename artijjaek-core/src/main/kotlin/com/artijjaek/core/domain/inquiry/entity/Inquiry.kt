package com.artijjaek.core.domain.inquiry.entity

import com.artijjaek.core.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
class Inquiry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    var id: Long? = null,

    var content: String,

    ) : BaseEntity() {

}