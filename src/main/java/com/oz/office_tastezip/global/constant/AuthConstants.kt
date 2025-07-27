package com.oz.office_tastezip.global.constant

object AuthConstants {

    object Header {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val JWT_TOKEN_PREFIX = "Bearer "
    }

    object RedisKey {
        const val JWT_KEY_PREFIX = "otz:token:"
        const val EMAIL_KEY_PREFIX = "otz:email:"
        const val EMAIL_ATTEMPT_KEY_PREFIX = "otz:attempt:email:"
        const val EMAIL_VERIFY_KEY_PREFIX = "otz:verify:email:"
    }

    object Jwt {
        const val AUTHORITIES_KEY = "auth"
        const val SERIAL_KEY = "serial"
    }
}
