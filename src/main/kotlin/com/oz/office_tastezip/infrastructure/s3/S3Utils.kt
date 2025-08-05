package com.oz.office_tastezip.infrastructure.s3

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Component
class S3Utils(
    private val s3Client: S3Client,
    @Value("\${s3.bucket}") private val bucket: String
) {

    private val log = KotlinLogging.logger {}

    fun upload(file: MultipartFile): String {
        val key = UUID.randomUUID().toString() + "_" + file.originalFilename

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.contentType)
                .build(),
            RequestBody.fromBytes(file.bytes)
        )

        log.info { "Uploaded file key: $key" }
        return key
    }

    fun delete(key: String) {
        log.info { "Deleted file key: $key" }
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        )
    }

}
