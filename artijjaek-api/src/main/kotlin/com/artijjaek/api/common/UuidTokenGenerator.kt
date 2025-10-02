package com.artijjaek.api.common

import java.util.*

class UuidTokenGenerator {

    companion object {
        fun generatorUuidToken(): String {
            return UUID.randomUUID().toString()
        }
    }

}