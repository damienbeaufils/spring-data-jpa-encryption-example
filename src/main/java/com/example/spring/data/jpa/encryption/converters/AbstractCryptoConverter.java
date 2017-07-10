package com.example.spring.data.jpa.encryption.converters;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.AttributeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.example.spring.data.jpa.encryption.converters.KeyProperty.DATABASE_ENCRYPTION_KEY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

abstract class AbstractCryptoConverter<T> implements AttributeConverter<T, String> {

    private CipherInitializer cipherInitializer;

    public AbstractCryptoConverter() {
        this(new CipherInitializer());
    }

    public AbstractCryptoConverter(CipherInitializer cipherInitializer) {
        this.cipherInitializer = cipherInitializer;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (isNotEmpty(DATABASE_ENCRYPTION_KEY) && isNotNullOrEmpty(attribute)) {
            try {
                Cipher cipher = cipherInitializer.prepareAndInitCipher(Cipher.ENCRYPT_MODE, DATABASE_ENCRYPTION_KEY);
                return encrypt(cipher, attribute);
            } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }
        }
        return entityAttributeToString(attribute);
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (isNotEmpty(DATABASE_ENCRYPTION_KEY) && isNotEmpty(dbData)) {
            try {
                Cipher cipher = cipherInitializer.prepareAndInitCipher(Cipher.DECRYPT_MODE, DATABASE_ENCRYPTION_KEY);
                return decrypt(cipher, dbData);
            } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }
        }
        return stringToEntityAttribute(dbData);
    }

    abstract boolean isNotNullOrEmpty(T attribute);

    abstract T stringToEntityAttribute(String dbData);

    abstract String entityAttributeToString(T attribute);

    byte[] callCipherDoFinal(Cipher cipher, byte[] bytes) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(bytes);
    }

    private String encrypt(Cipher cipher, T attribute) throws IllegalBlockSizeException, BadPaddingException {
        byte[] bytesToEncrypt = entityAttributeToString(attribute).getBytes();
        byte[] encryptedBytes = callCipherDoFinal(cipher, bytesToEncrypt);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private T decrypt(Cipher cipher, String dbData) throws IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedBytes = Base64.getDecoder().decode(dbData);
        byte[] decryptedBytes = callCipherDoFinal(cipher, encryptedBytes);
        return stringToEntityAttribute(new String(decryptedBytes));
    }
}
