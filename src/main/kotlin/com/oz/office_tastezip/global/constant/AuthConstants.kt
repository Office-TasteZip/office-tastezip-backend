package com.oz.office_tastezip.global.constant

object AuthConstants {

    object RedisKey {
        const val JWT_KEY_PREFIX = "otz:token:"
    }

    object Jwt {
        const val AUTHORITIES_KEY = "auth"
        const val SERIAL_KEY = "serial"
    }
}
