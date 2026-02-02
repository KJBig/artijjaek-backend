package com.artijjaek.admin.service

import com.artijjaek.admin.common.util.PasswordEncoder
import com.artijjaek.admin.dto.request.PatchPasswordRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.ADMIN_PASSWORD_NOT_MATCH_ERROR
import com.artijjaek.core.domain.admin.service.AdminDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val adminDomainService: AdminDomainService,
) {

    @Transactional
    fun changeAdminPassword(adminId: Long, request: PatchPasswordRequest) {
        val admin = adminDomainService.findById(adminId)

        if (!PasswordEncoder.isMatch(request.oldPassword, admin.password)) {
            throw ApplicationException(ADMIN_PASSWORD_NOT_MATCH_ERROR)
        }

        val newPassword = PasswordEncoder.passwordEncode(request.newPassword)
        admin.changePassword(newPassword)
    }

}