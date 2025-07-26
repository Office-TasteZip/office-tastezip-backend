package com.oz.office_tastezip.global.constant;

public class AuthConstants {

    public static class Header {
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String JWT_TOKEN_PREFIX = "Bearer ";
    }

    public static class RedisKey {
        public static final String AUTH_KEY_PREFIX = "otz:%s:%s";
        public static final String JWT_KEY_PREFIX = "otz:token:";
    }

    public static class AuthResult {
        public static final String AUTHNUM_CORRECT_ANSWER = "AUTHNUM_CORRECT_ANSWER";
        public static final String INCREASE_FAIL_COUNT = "increase_fail_count";
        public static final String LOGIN_SUCCESS = "login_success";
        public static final String RESET_LOGIN_COUNT = "fail_count_reset";
    }

    public static class Jwt {
        public static final String AUTHORITIES_KEY = "auth";
        public static final String SERIAL_KEY = "serial";
    }

}
