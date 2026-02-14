package com.artijjaek.core.common.mail.dto

import com.artijjaek.core.domain.member.entity.Member

data class MemberAlertDto(val email: String?, val nickname: String, val uuidToken: String) {

    companion object {
        fun from(member: Member): MemberAlertDto {
            return MemberAlertDto(
                email = member.email,
                nickname = member.nickname,
                uuidToken = member.uuidToken
            )
        }
    }

}
