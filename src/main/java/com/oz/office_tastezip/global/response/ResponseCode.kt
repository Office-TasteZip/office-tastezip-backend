package com.oz.office_tastezip.global.response

import org.springframework.http.HttpStatus

enum class ResponseCode(
    val code: String,
    val message: String,
    val httpStatus: HttpStatus
) {
    // 0xxx: General Success/Fail
    SUCCESS("0000", "Success", HttpStatus.OK),
    FAIL("0001", "Request failed to process", HttpStatus.BAD_REQUEST),

    // 1xxx: Authentication & Authorization
    UNAUTHORIZED("1001", "Authentication is required", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("1002", "You do not have permission", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("1003", "Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("1004", "Invalid token", HttpStatus.UNAUTHORIZED),
    TOKEN_DOES_NOT_EXIST("1005", "Token does not exist", HttpStatus.UNAUTHORIZED),

    // 2xxx: User Related
    USER_NOT_FOUND("2001", "User does not exist", HttpStatus.NOT_FOUND),
    DATA_NOT_FOUND("2002", "Data does not exist", HttpStatus.NOT_FOUND),
    DUPLICATED_EMAIL("2003", "Email is already in use", HttpStatus.NOT_FOUND),
    DATA_EXIST("2004", "Data is already exist", HttpStatus.CONFLICT),
    INVALID_PASSWORD("2005", "Invalid password", HttpStatus.NOT_FOUND),
    ACCOUNT_LOCK("2006", "User account is locked", HttpStatus.LOCKED),

    // 3xxx: Validation
    VALIDATION_ERROR("3001", "Invalid request input", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("3002", "Missing required field", HttpStatus.BAD_REQUEST),

    // 4xxx: Resource/Entity
    RESOURCE_NOT_FOUND("4001", "Requested resource not found", HttpStatus.NOT_FOUND),
    DUPLICATED_RESOURCE("4002", "Resource already exists", HttpStatus.CONFLICT),

    // 5xxx: Server/System
    INTERNAL_ERROR("5000", "Internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    DB_ERROR("5001", "Database error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_API_ERROR("5002", "External API request failed", HttpStatus.BAD_GATEWAY)
}
