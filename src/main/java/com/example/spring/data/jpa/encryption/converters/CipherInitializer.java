package com.example.spring.data.jpa.encryption.converters;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

public class CipherInitializer {

    private static final String CIPHER_INSTANCE_NAME = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "AES";

    public Cipher prepareAndInitCipher(int encryptionMode, String key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
        Key secretKey = new SecretKeySpec(key.getBytes(), SECRET_KEY_ALGORITHM);
        AlgorithmParameterSpec algorithmParameters = getAlgorithmParameterSpec(cipher);

        callCipherInit(cipher, encryptionMode, secretKey, algorithmParameters);
        return cipher;
    }

    void callCipherInit(Cipher cipher, int encryptionMode, Key secretKey, AlgorithmParameterSpec algorithmParameters) throws InvalidKeyException, InvalidAlgorithmParameterException {
        cipher.init(encryptionMode, secretKey, algorithmParameters);
    }

    int getCipherBlockSize(Cipher cipher) {
        return cipher.getBlockSize();
    }

    private AlgorithmParameterSpec getAlgorithmParameterSpec(Cipher cipher) {
        byte[] iv = new byte[getCipherBlockSize(cipher)];
        return new IvParameterSpec(iv);
    }

}
