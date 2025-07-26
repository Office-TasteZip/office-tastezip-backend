package com.oz.office_tastezip.global.constant

object AuthConstants {

    object Header {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val JWT_TOKEN_PREFIX = "Bearer "
    }

    object RedisKey {
        const val AUTH_KEY_PREFIX = "otz:%s:%s"
        const val JWT_KEY_PREFIX = "otz:token:"
    }

    object Jwt {
        const val AUTHORITIES_KEY = "auth"
        const val SERIAL_KEY = "serial"
    }
}
