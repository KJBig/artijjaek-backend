package com.artijjaek.admin.service

import com.artijjaek.core.domain.admin.service.AdminDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class AdminRefreshTokenService(
    private val adminDomainService: AdminDomainService,
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun clearRefreshToken(adminId: Long) {
        val admin = adminDomainService.findById(adminId)
        admin.changeRefreshToken(null)
    }

}