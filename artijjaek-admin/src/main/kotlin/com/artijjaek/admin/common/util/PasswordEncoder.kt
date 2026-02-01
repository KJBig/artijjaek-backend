package com.artijjaek.admin.common.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordEncoder {

    companion object {

        fun passwordEncode(password: String): String {
            val encoder = BCryptPasswordEncoder()
            return encoder.encode(password)
        }

        fun isMatch(password: String, encodePassword: String): Boolean {
            val encoder = BCryptPasswordEncoder()
            return encoder.matches(password, encodePassword)
        }

    }

}