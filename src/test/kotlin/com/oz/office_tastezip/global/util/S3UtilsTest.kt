package com.oz.office_tastezip.global.util

import com.oz.office_tastezip.infrastructure.s3.S3Properties
import com.oz.office_tastezip.infrastructure.s3.S3Utils
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.mock.web.MockMultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

class S3UtilsTest : StringSpec({

    val s3Client = mockk<S3Client>(relaxed = true)
    val s3Properties = mockk<S3Properties>(relaxed = true)
    every { s3Properties.bucket } returns "test-bucket"

    val s3Utils = S3Utils(s3Client, s3Properties)

    "이미지 업로드" {
        val content = "Image upload test".toByteArray()
        val mockFile = MockMultipartFile("file", "test.png", "image/png", content)

        val resultKey = s3Utils.upload(mockFile)

        resultKey shouldStartWith Regex("""[a-f0-9\-]{36}_test\.png""")

        verify(exactly = 1) {
            s3Client.putObject(
                withArg<PutObjectRequest> {
                    it.bucket() shouldBe s3Properties.bucket
                    it.key() shouldBe resultKey
                    it.contentType() shouldBe "image/png"
                },
                any<RequestBody>()
            )
        }
    }

    "이미지 삭제" {
        val key = UUID.randomUUID().toString() + "_test.jpg"

        s3Utils.delete(key)

        val deleteSlot = slot<DeleteObjectRequest>()

        verify(exactly = 1) {
            s3Client.deleteObject(capture(deleteSlot))
        }

        deleteSlot.captured.bucket() shouldBe "test-bucket"
        deleteSlot.captured.key() shouldBe key

    }
})
