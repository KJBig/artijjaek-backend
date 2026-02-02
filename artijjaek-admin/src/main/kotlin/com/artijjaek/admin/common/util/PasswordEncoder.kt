package com.artijjaek.admin.common.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordEncoder {

    companion object {
        
        private val encoder = BCryptPasswordEncoder()

        fun passwordEncode(password: String): String {
            return encoder.encode(password)
        }

        fun isMatch(password: String, encodePassword: String): Boolean {
            return encoder.matches(password, encodePassword)
        }

    }

}