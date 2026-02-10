package com.artijjaek.admin.service

import com.artijjaek.admin.common.util.PasswordEncoder
import com.artijjaek.admin.dto.request.PatchPasswordRequest
import com.artijjaek.core.common.error.ApplicationException
import com.artijjaek.core.common.error.ErrorCode.ADMIN_PASSWORD_NOT_MATCH_ERROR
import com.artijjaek.core.domain.admin.entity.Admin
import com.artijjaek.core.domain.admin.enums.AdminRole
import com.artijjaek.core.domain.admin.service.AdminDomainService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class AdminServiceTest {

    @InjectMockKs
    lateinit var adminService: AdminService

    @MockK
    lateinit var adminDomainService: AdminDomainService

    @Test
    @DisplayName("관리자 비밀번호를 변경할 수 있다")
    fun changeAdminPasswordTest() {
        // given
        val request = PatchPasswordRequest(
            oldPassword = "old-password",
            newPassword = "new-password"
        )
        val admin = Admin(
            id = 1L,
            name = "관리자",
            email = "admin@test.com",
            password = PasswordEncoder.passwordEncode("old-password"),
            adminRole = AdminRole.SUPER_ADMIN,
            refreshToken = null
        )
        every { adminDomainService.findById(1L) } returns admin

        // when
        adminService.changeAdminPassword(1L, request)

        // then
        assertThat(PasswordEncoder.isMatch("new-password", admin.password)).isTrue()
    }

    @Test
    @DisplayName("기존 비밀번호가 일치하지 않으면 예외가 발생한다")
    fun changeAdminPasswordNotMatchTest() {
        // given
        val request = PatchPasswordRequest(
            oldPassword = "wrong-password",
            newPassword = "new-password"
        )
        val admin = Admin(
            id = 1L,
            name = "관리자",
            email = "admin@test.com",
            password = PasswordEncoder.passwordEncode("old-password"),
            adminRole = AdminRole.SUPER_ADMIN,
            refreshToken = null
        )
        every { adminDomainService.findById(1L) } returns admin

        // when
        val exception = assertThrows<ApplicationException> {
            adminService.changeAdminPassword(1L, request)
        }

        // then
        assertThat(exception.code).isEqualTo(ADMIN_PASSWORD_NOT_MATCH_ERROR.code)
        assertThat(exception.message).isEqualTo(ADMIN_PASSWORD_NOT_MATCH_ERROR.message)
    }
}
