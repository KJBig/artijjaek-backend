package com.artijjaek.core.common.mail.dto

import com.artijjaek.core.domain.member.entity.Member

data class MemberMailDto(val email: String, val nickname: String, val uuidToken: String) {

    companion object {
        fun from(member: Member): MemberMailDto {
            return MemberMailDto(
                email = member.email,
                nickname = member.nickname,
                uuidToken = member.uuidToken
            )
        }
    }

}
