package com.artijjaek.core.domain.admin.repository

import com.artijjaek.core.domain.admin.entity.Admin
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepository : JpaRepository<Admin, Long> {
    fun findByEmail(email: String): Admin?
}