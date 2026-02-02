package com.artijjaek.core.domain.admin.service

import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.ADMIN_NOT_FOUND_ERROR
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

    fun findById(adminId: Long): Admin {
        return adminRepository.findById(adminId).orElseThrow { ApplicationException(ADMIN_NOT_FOUND_ERROR) }
    }

}