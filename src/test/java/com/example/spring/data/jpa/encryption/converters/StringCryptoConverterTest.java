package com.example.spring.data.jpa.encryption.converters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.AdditionalAnswers.returnsSecondArg;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StringCryptoConverterTest {

    private static final String STRING_TO_CIPHER = "ma_chaine_a_chiffrer";
    private static final String STRING_TO_DECIPHER = "bWFfY2hhaW5lX2FfY2hpZmZyZXI=";

    private StringCryptoConverter stringCryptoConverter;

    private StringCryptoConverter spiedStringCryptoConverter;

    @Mock
    private CipherInitializer cipherInitializer;

    @Before
    public void setUp() throws Exception {
        stringCryptoConverter = new StringCryptoConverter(cipherInitializer);

        spiedStringCryptoConverter = spy(stringCryptoConverter);
        doAnswer(returnsSecondArg()).when(spiedStringCryptoConverter).callCipherDoFinal(any(Cipher.class), any(byte[].class));

        KeyProperty.DATABASE_ENCRYPTION_KEY = "MySuperSecretKey";
    }

    @Test
    public void convertToDatabaseColumn_should_return_null_string_when_string_to_encrypt_is_null() throws Exception {
        // Given
        String attribute = null;

        // When
        String result = stringCryptoConverter.convertToDatabaseColumn(attribute);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void convertToDatabaseColumn_should_return_empty_string_when_string_to_encrypt_is_empty() throws Exception {
        // Given
        String attribute = "";

        // When
        String result = stringCryptoConverter.convertToDatabaseColumn(attribute);

        // Then
        assertThat(result).isEqualTo(attribute);
    }

    @Test
    public void convertToDatabaseColumn_should_return_encrypted_string_as_base_64() throws Exception {
        // Given
        Cipher cipher = mock(Cipher.class);
        when(cipherInitializer.prepareAndInitCipher(Cipher.ENCRYPT_MODE, KeyProperty.DATABASE_ENCRYPTION_KEY)).thenReturn(cipher);

        // When
        String result = spiedStringCryptoConverter.convertToDatabaseColumn(STRING_TO_CIPHER);

        // Then
        verify(spiedStringCryptoConverter).callCipherDoFinal(cipher, STRING_TO_CIPHER.getBytes());
        assertThat(result).isEqualTo(STRING_TO_DECIPHER);
    }

    @Test
    public void convertToDatabaseColumn_should_return_unchanged_string_when_database_encryption_key_is_null() throws Exception {
        // Given
        KeyProperty.DATABASE_ENCRYPTION_KEY = null;

        // When
        String result = spiedStringCryptoConverter.convertToDatabaseColumn(STRING_TO_CIPHER);

        // Then
        assertThat(result).isEqualTo(STRING_TO_CIPHER);
    }

    @Test
    public void convertToDatabaseColumn_should_return_unchanged_string_when_database_encryption_key_is_empty() throws Exception {
        // Given
        KeyProperty.DATABASE_ENCRYPTION_KEY = "";

        // When
        String result = spiedStringCryptoConverter.convertToDatabaseColumn(STRING_TO_CIPHER);

        // Then
        assertThat(result).isEqualTo(STRING_TO_CIPHER);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_cipher_initialization_fails_with_InvalidKeyException() throws Exception {
        // Given
        InvalidKeyException invalidKeyException = new InvalidKeyException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(invalidKeyException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToDatabaseColumn(STRING_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(invalidKeyException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_cipher_initialization_fails_with_NoSuchAlgorithmException() throws Exception {
        // Given
        NoSuchAlgorithmException noSuchAlgorithmException = new NoSuchAlgorithmException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(noSuchAlgorithmException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToDatabaseColumn(STRING_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(noSuchAlgorithmException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_cipher_initialization_fails_with_NoSuchPaddingException() throws Exception {
        // Given
        NoSuchPaddingException noSuchPaddingException = new NoSuchPaddingException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(noSuchPaddingException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToDatabaseColumn(STRING_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(noSuchPaddingException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_encryption_fails_with_BadPaddingException() throws Exception {
        // Given
        BadPaddingException badPaddingException = new BadPaddingException();
        when(spiedStringCryptoConverter.callCipherDoFinal(any(Cipher.class), any(byte[].class))).thenThrow(badPaddingException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToDatabaseColumn(STRING_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(badPaddingException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_encryption_fails_with_IllegalBlockSizeException() throws Exception {
        // Given
        IllegalBlockSizeException illegalBlockSizeException = new IllegalBlockSizeException();
        when(spiedStringCryptoConverter.callCipherDoFinal(any(Cipher.class), any(byte[].class))).thenThrow(illegalBlockSizeException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToDatabaseColumn(STRING_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(illegalBlockSizeException);
    }

    @Test
    public void convertToEntityAttribute_should_return_null_string_when_string_to_decrypt_is_null() throws Exception {
        // Given
        String dbData = null;

        // When
        String result = stringCryptoConverter.convertToEntityAttribute(dbData);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void convertToEntityAttribute_should_return_empty_string_when_string_to_decrypt_is_empty() throws Exception {
        // Given
        String dbData = "";

        // When
        String result = stringCryptoConverter.convertToEntityAttribute(dbData);

        // Then
        assertThat(result).isEqualTo(dbData);
    }

    @Test
    public void convertToEntityAttribute_should_return_decrypted_string() throws Exception {
        // Given
        Cipher cipher = mock(Cipher.class);
        when(cipherInitializer.prepareAndInitCipher(Cipher.DECRYPT_MODE, KeyProperty.DATABASE_ENCRYPTION_KEY)).thenReturn(cipher);

        // When
        String result = spiedStringCryptoConverter.convertToEntityAttribute(STRING_TO_DECIPHER);

        // Then
        verify(spiedStringCryptoConverter).callCipherDoFinal(cipher, STRING_TO_CIPHER.getBytes());
        assertThat(result).isEqualTo(STRING_TO_CIPHER);
    }

    @Test
    public void convertToEntityAttribute_should_return_unchanged_string_when_database_encryption_key_is_null() throws Exception {
        // Given
        KeyProperty.DATABASE_ENCRYPTION_KEY = null;

        // When
        String result = spiedStringCryptoConverter.convertToEntityAttribute(STRING_TO_DECIPHER);

        // Then
        assertThat(result).isEqualTo(STRING_TO_DECIPHER);
    }

    @Test
    public void convertToEntityAttribute_should_return_unchanged_string_when_database_encryption_key_is_empty() throws Exception {
        // Given
        KeyProperty.DATABASE_ENCRYPTION_KEY = "";

        // When
        String result = spiedStringCryptoConverter.convertToEntityAttribute(STRING_TO_DECIPHER);

        // Then
        assertThat(result).isEqualTo(STRING_TO_DECIPHER);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_cipher_initialization_fails_with_InvalidKeyException() throws Exception {
        // Given
        InvalidKeyException invalidKeyException = new InvalidKeyException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(invalidKeyException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToEntityAttribute(STRING_TO_DECIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(invalidKeyException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_cipher_initialization_fails_with_NoSuchAlgorithmException() throws Exception {
        // Given
        NoSuchAlgorithmException noSuchAlgorithmException = new NoSuchAlgorithmException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(noSuchAlgorithmException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToEntityAttribute(STRING_TO_DECIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(noSuchAlgorithmException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_cipher_initialization_fails_with_NoSuchPaddingException() throws Exception {
        // Given
        NoSuchPaddingException noSuchPaddingException = new NoSuchPaddingException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(noSuchPaddingException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToEntityAttribute(STRING_TO_DECIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(noSuchPaddingException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_decryption_fails_with_BadPaddingException() throws Exception {
        // Given
        BadPaddingException badPaddingException = new BadPaddingException();
        when(spiedStringCryptoConverter.callCipherDoFinal(any(Cipher.class), any(byte[].class))).thenThrow(badPaddingException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToEntityAttribute(STRING_TO_DECIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(badPaddingException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_decryption_fails_with_IllegalBlockSizeException() throws Exception {
        // Given
        IllegalBlockSizeException illegalBlockSizeException = new IllegalBlockSizeException();
        when(spiedStringCryptoConverter.callCipherDoFinal(any(Cipher.class), any(byte[].class))).thenThrow(illegalBlockSizeException);

        // When
        Throwable throwable = catchThrowable(() -> spiedStringCryptoConverter.convertToEntityAttribute(STRING_TO_DECIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(illegalBlockSizeException);
    }

}
