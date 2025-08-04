package com.oz.office_tastezip.global.util

import com.oz.office_tastezip.global.exception.RequestFailureException
import mu.KotlinLogging
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

object RSAUtils {

    private val logger = KotlinLogging.logger {}
    private const val KEY_SIZE = 2048

    /**
     * 공개키/개인키 쌍을 문자열 Map으로 생성
     */
    fun generateRsaKeyMap(): Map<String, String> {
        return try {
            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(KEY_SIZE, SecureRandom())
            val keyPair = keyGen.generateKeyPair()

            val publicKey = Base64.getEncoder().encodeToString(keyPair.public.encoded)
            val privateKey = Base64.getEncoder().encodeToString(keyPair.private.encoded)

            mapOf("publicKey" to publicKey, "privateKey" to privateKey)
        } catch (e: NoSuchAlgorithmException) {
            logger.error(e) { "RSA 키쌍 생성 실패: ${e.message}" }
            throw RequestFailureException("RSA 키 생성에 실패했습니다.")
        }
    }

    /**
     * RSA 공개키 기반 암호화
     */
    fun encrypt(plainText: String, base64PublicKey: String): String {
        return try {
            val publicKey = getPublicKeyFromBase64(base64PublicKey)
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
            Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (e: Exception) {
            logger.error(e) { "암호화 실패: ${e.message}" }
            throw RequestFailureException("RSA 암호화에 실패했습니다.")
        }
    }

    /**
     * RSA 개인키 기반 복호화
     */
    fun decrypt(encryptedText: String, base64PrivateKey: String): String {
        return try {
            val privateKey = getPrivateKeyFromBase64(base64PrivateKey)
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            logger.error(e) { "복호화 실패: ${e.message}" }
            throw RequestFailureException("RSA 복호화에 실패했습니다.")
        }
    }

    private fun getPublicKeyFromBase64(base64Key: String): PublicKey {
        val decoded = Base64.getDecoder().decode(base64Key)
        val keySpec = X509EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }

    private fun getPrivateKeyFromBase64(base64Key: String): PrivateKey {
        val decoded = Base64.getDecoder().decode(base64Key)
        val keySpec = PKCS8EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }
}
