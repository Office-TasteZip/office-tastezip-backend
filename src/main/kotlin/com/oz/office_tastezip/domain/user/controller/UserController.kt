package com.oz.office_tastezip.domain.user.controller

import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose.SIGNUP
import com.oz.office_tastezip.domain.organization.OrganizationService
import com.oz.office_tastezip.domain.user.UserService
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserInsertRequest
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserUpdateRequest
import com.oz.office_tastezip.domain.user.dto.UserResponseDto
import com.oz.office_tastezip.global.exception.RequestFailureException
import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseSuccess
import com.oz.office_tastezip.global.util.FileValidationUtils
import com.oz.office_tastezip.global.util.RedisUtils
import com.oz.office_tastezip.global.util.SecurityUtils.getAuthenticatedUserDetail
import com.oz.office_tastezip.infrastructure.s3.S3Utils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Tag(name = "사용자 컨트롤러", description = "USER CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/users")
class UserController(
    private val s3Utils: S3Utils,
    private val redisUtils: RedisUtils,
    private val userService: UserService,
    private val organizationService: OrganizationService
) {

    private val log = KotlinLogging.logger {}

    @Operation(summary = "회원 가입")
    @PostMapping("/register")
    fun register(@RequestBody @Valid userInsertRequest: UserInsertRequest): ResponseEntity<Response.Body<String>> {
        if (userInsertRequest.password != userInsertRequest.confirmPassword) {
            log.info {
                "User register failed: password and confirmation do not match. " +
                        "password: ${userInsertRequest.password}, password confirm: ${userInsertRequest.confirmPassword}"
            }
            throw RequestFailureException("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
        }

        val email = userInsertRequest.email
        require(
            redisUtils.get("${SIGNUP.verifyKeyPrefix}$email") as? String == "complete"
        ) { throw RequestFailureException("이메일 인증이 완료되지 않았습니다.") }

        val organization = organizationService.findOrCreateOrganization(
            email.substringAfter("@"),
            userInsertRequest.organizationName
        )

        userService.register(userInsertRequest, organization)
        return ResponseSuccess<String>().success("회원가입 되었습니다.")
    }

    @Operation(summary = "내정보 조회")
    @GetMapping("/my-info")
    fun getMyInfo(): ResponseEntity<Response.Body<UserResponseDto>> {
        val user = userService.findByUserUUID(getAuthenticatedUserDetail().uuid)
        return ResponseSuccess<UserResponseDto>().success(
            UserResponseDto.of(
                user,
                organizationService.findOrganization(user.organization).organizationName
            )
        )
    }

    @Operation(summary = "사용자 정보 수정")
    @PutMapping("/update")
    fun update(@RequestBody @Valid userUpdateRequest: UserUpdateRequest): ResponseEntity<Response.Body<String>> {
        userService.update(userUpdateRequest)
        return ResponseSuccess<String>().success("정보 수정 되었습니다.")
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/withdraw")
    fun withdraw(): ResponseEntity<Response.Body<String>> {
        userService.withdraw(getAuthenticatedUserDetail().uuid)
        return ResponseSuccess<String>().success("회원 탈퇴 되었습니다.")
    }

    @Operation(
        summary = "사용자 프로필 사진 등록(수정)",
        description = "프로필 이미지를 multipart/form-data로 업로드합니다."
    )
    @Parameters(
        Parameter(
            name = "multipartFile",
            description = "업로드할 프로필 이미지 파일",
            required = true,
            content = [Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)]
        )
    )
    @PutMapping("/profile-image")
    fun updateProfileImage(@RequestParam multipartFile: MultipartFile): ResponseEntity<Response.Body<String>> {
        val userDetails = getAuthenticatedUserDetail()
        val userId = UUID.fromString(userDetails.uuid)
        val currentImagePath = userDetails.profileImageUrl

        FileValidationUtils.validateImageFile(multipartFile)

        val newKey = s3Utils.upload(multipartFile)

        deleteIfExists(currentImagePath)
        userService.updateProfileImage(userId, newKey)

        return ResponseSuccess<String>().success("프로필 이미지가 수정 되었습니다.")
    }


    @Operation(summary = "사용자 프로필 사진 삭제")
    @DeleteMapping("/profile-image")
    fun deleteProfileImage(): ResponseEntity<Response.Body<String>> {
        val userDetails = getAuthenticatedUserDetail()
        val userId = UUID.fromString(userDetails.uuid)
        val currentImagePath = userDetails.profileImageUrl

        deleteIfExists(currentImagePath)
        userService.updateProfileImage(userId, null)

        return ResponseSuccess<String>().success("프로필 이미지가 삭제 되었습니다.")
    }

    private fun deleteIfExists(path: String?) {
        path?.takeIf { it.isNotBlank() }?.let {
            runCatching { s3Utils.delete(it) }
                .onFailure { e -> log.warn("기존 이미지 삭제 실패: $e") }
        }
    }

}
