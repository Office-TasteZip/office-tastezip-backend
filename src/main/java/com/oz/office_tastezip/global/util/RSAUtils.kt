package com.oz.office_tastezip.global.util;

import com.oz.office_tastezip.global.exception.RequestFailureException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class RSAUtils {

    private static final int KEY_SIZE = 2048;

    /**
     * 공개키/개인키 쌍을 문자열(Map)로 생성
     */
    public static Map<String, String> generateRsaKeyMap() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyGen.generateKeyPair();

            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            return Map.of("publicKey", publicKey, "privateKey", privateKey);
        } catch (NoSuchAlgorithmException e) {
            log.error("RSA 키쌍 생성 실패: {}", e.getMessage(), e);
            throw new RequestFailureException("RSA 키 생성에 실패했습니다.");
        }
    }

    /**
     * RSA 공개키 기반 암호화
     */
    public static String encrypt(String plainText, String base64PublicKey) {
        try {
            PublicKey publicKey = getPublicKeyFromBase64(base64PublicKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("암호화 실패: {}", e.getMessage(), e);
            throw new RequestFailureException("RSA 암호화에 실패했습니다.");
        }
    }

    /**
     * RSA 개인키 기반 복호화
     */
    public static String decrypt(String encryptedText, String base64PrivateKey) {
        try {
            PrivateKey privateKey = getPrivateKeyFromBase64(base64PrivateKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("복호화 실패: {}", e.getMessage(), e);
            throw new RequestFailureException("RSA 복호화에 실패했습니다.");
        }
    }

    private static PublicKey getPublicKeyFromBase64(String base64Key) throws GeneralSecurityException {
        byte[] decoded = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    private static PrivateKey getPrivateKeyFromBase64(String base64Key) throws GeneralSecurityException {
        byte[] decoded = Base64.getDecoder().decode(base64Key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}
