package com.artijjaek.core.domain.admin.service

import com.artijjaek.core.domain.admin.entity.Admin
import com.artijjaek.core.domain.admin.repository.AdminRepository
import org.springframework.stereotype.Service

@Service
class AdminDomainService(
    private val adminRepository: AdminRepository,
) {

    fun findByEmail(email: String): Admin? {
        return adminRepository.findByEmail(email)
    }

}