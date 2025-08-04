package com.oz.office_tastezip.global.util

import com.oz.office_tastezip.global.exception.ValidationFailureException
import org.springframework.web.multipart.MultipartFile

object FileValidationUtils {

    private val allowedTypes = mapOf(
        "image/jpeg" to listOf("jpg", "jpeg"),
        "image/png" to listOf("png"),
        "image/webp" to listOf("webp")
    )

    fun validateImageFile(file: MultipartFile) {
        if (file.isEmpty) throw ValidationFailureException("이미지 파일이 누락되었습니다.")

        val contentType = file.contentType
            ?: throw ValidationFailureException("Content-Type이 누락되었습니다.")

        val originalFilename = file.originalFilename?.lowercase()
            ?: throw ValidationFailureException("파일 이름이 유효하지 않습니다.")

        val extension = originalFilename.substringAfterLast('.', missingDelimiterValue = "")

        if (contentType !in allowedTypes.keys) {
            throw ValidationFailureException("지원하지 않는 이미지 타입입니다. (${allowedTypes.keys.joinToString()} 파일만 업로드 가능합니다.)")
        }

        if (extension !in allowedTypes[contentType]!!) {
            throw ValidationFailureException("확장자가 Content-Type과 일치하지 않거나 허용되지 않았습니다. (${allowedTypes[contentType]!!.joinToString()})")
        }
    }

}
