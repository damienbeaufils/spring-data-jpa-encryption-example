package com.example.spring.data.jpa.encryption.converters;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class CipherInitializer {

    private static final String CIPHER_INSTANCE_NAME = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "AES";

    public Cipher prepareAndInitCipher(int encryptionMode, String key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
        Key secretKey = new SecretKeySpec(key.getBytes(), SECRET_KEY_ALGORITHM);
        callCipherInit(cipher, encryptionMode, secretKey);
        return cipher;
    }

    void callCipherInit(Cipher cipher, int encryptionMode, Key secretKey) throws InvalidKeyException {
        cipher.init(encryptionMode, secretKey);
    }

}
