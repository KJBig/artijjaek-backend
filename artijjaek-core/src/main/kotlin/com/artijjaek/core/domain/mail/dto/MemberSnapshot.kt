package com.artijjaek.core.domain.mail.dto

import com.artijjaek.core.common.mail.dto.MemberAlertDto

data class MemberSnapshot(
    val email: String,
    val nickname: String,
    val uuidToken: String,
) {
    companion object {
        fun from(memberData: MemberAlertDto): MemberSnapshot {
            return MemberSnapshot(
                email = memberData.email!!,
                nickname = memberData.nickname,
                uuidToken = memberData.uuidToken
            )
        }
    }
}
