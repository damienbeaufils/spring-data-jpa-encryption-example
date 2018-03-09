package com.example.spring.data.jpa.encryption.converters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.AdditionalAnswers.returnsSecondArg;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalDateTimeCryptoConverterTest {

    private static final LocalDateTime LOCAL_DATE_TIME_TO_CIPHER = LocalDateTime.of(2017, 3, 28, 16, 25, 46);
    private static final String LOCAL_DATE_TIME_TO_CIPHER_AS_STRING = LOCAL_DATE_TIME_TO_CIPHER.format(DateTimeFormatter.ISO_DATE_TIME);
    private static final String LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING = "MjAxNy0wMy0yOFQxNjoyNTo0Ng==";

    private LocalDateTimeCryptoConverter localDateTimeCryptoConverter;

    private LocalDateTimeCryptoConverter spiedLocalDateTimeCryptoConverter;

    @Mock
    private CipherInitializer cipherInitializer;

    @Before
    public void setUp() throws Exception {
        localDateTimeCryptoConverter = new LocalDateTimeCryptoConverter(cipherInitializer);

        spiedLocalDateTimeCryptoConverter = spy(localDateTimeCryptoConverter);
        doAnswer(returnsSecondArg()).when(spiedLocalDateTimeCryptoConverter).callCipherDoFinal(any(Cipher.class), any(byte[].class));

        KeyProperty.DATABASE_ENCRYPTION_KEY = "MySuperSecretKey";
    }

    @Test
    public void convertToDatabaseColumn_should_return_null_string_when_local_date_time_to_encrypt_is_null() throws Exception {
        // Given
        LocalDateTime attribute = null;

        // When
        String result = localDateTimeCryptoConverter.convertToDatabaseColumn(attribute);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void convertToDatabaseColumn_should_return_encrypted_string_as_base_64() throws Exception {
        // Given
        Cipher cipher = mock(Cipher.class);
        when(cipherInitializer.prepareAndInitCipher(Cipher.ENCRYPT_MODE, KeyProperty.DATABASE_ENCRYPTION_KEY)).thenReturn(cipher);

        // When
        String result = spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER);

        // Then
        verify(spiedLocalDateTimeCryptoConverter).callCipherDoFinal(cipher, LOCAL_DATE_TIME_TO_CIPHER_AS_STRING.getBytes());
        assertThat(result).isEqualTo(LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING);
    }

    @Test
    public void convertToDatabaseColumn_should_return_formatted_local_date_time_but_not_encrypted_when_database_encryption_key_is_null() throws Exception {
        // Given
        KeyProperty.DATABASE_ENCRYPTION_KEY = null;

        // When
        String result = spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER);

        // Then
        assertThat(result).isEqualTo(LOCAL_DATE_TIME_TO_CIPHER_AS_STRING);
    }

    @Test
    public void convertToDatabaseColumn_should_return_formatted_local_date_time_but_not_encrypted_when_database_encryption_key_is_empty() throws Exception {
        // Given
        KeyProperty.DATABASE_ENCRYPTION_KEY = "";

        // When
        String result = spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER);

        // Then
        assertThat(result).isEqualTo(LOCAL_DATE_TIME_TO_CIPHER_AS_STRING);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_cipher_initialization_fails_with_InvalidKeyException() throws Exception {
        // Given
        InvalidKeyException invalidKeyException = new InvalidKeyException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(invalidKeyException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(invalidKeyException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_cipher_initialization_fails_with_NoSuchAlgorithmException() throws Exception {
        // Given
        NoSuchAlgorithmException noSuchAlgorithmException = new NoSuchAlgorithmException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(noSuchAlgorithmException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(noSuchAlgorithmException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_cipher_initialization_fails_with_NoSuchPaddingException() throws Exception {
        // Given
        NoSuchPaddingException noSuchPaddingException = new NoSuchPaddingException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(noSuchPaddingException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(noSuchPaddingException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_cipher_initialization_fails_with_InvalidAlgorithmParameterException() throws Exception {
        // Given
        InvalidAlgorithmParameterException invalidAlgorithmParameterException = new InvalidAlgorithmParameterException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(invalidAlgorithmParameterException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(invalidAlgorithmParameterException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_encryption_fails_with_BadPaddingException() throws Exception {
        // Given
        BadPaddingException badPaddingException = new BadPaddingException();
        when(spiedLocalDateTimeCryptoConverter.callCipherDoFinal(any(Cipher.class), any(byte[].class))).thenThrow(badPaddingException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(badPaddingException);
    }

    @Test
    public void convertToDatabaseColumn_should_rethrow_exception_when_encryption_fails_with_IllegalBlockSizeException() throws Exception {
        // Given
        IllegalBlockSizeException illegalBlockSizeException = new IllegalBlockSizeException();
        when(spiedLocalDateTimeCryptoConverter.callCipherDoFinal(any(Cipher.class), any(byte[].class))).thenThrow(illegalBlockSizeException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToDatabaseColumn(LOCAL_DATE_TIME_TO_CIPHER));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(illegalBlockSizeException);
    }

    @Test
    public void convertToEntityAttribute_should_return_null_local_date_time_when_string_to_decrypt_is_null() throws Exception {
        // Given
        String dbData = null;

        // When
        LocalDateTime result = localDateTimeCryptoConverter.convertToEntityAttribute(dbData);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void convertToEntityAttribute_should_return_null_local_date_time_when_string_to_decrypt_is_empty() throws Exception {
        // Given
        String dbData = "";

        // When
        LocalDateTime result = localDateTimeCryptoConverter.convertToEntityAttribute(dbData);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void convertToEntityAttribute_should_return_decrypted_string() throws Exception {
        // Given
        Cipher cipher = mock(Cipher.class);
        when(cipherInitializer.prepareAndInitCipher(Cipher.DECRYPT_MODE, KeyProperty.DATABASE_ENCRYPTION_KEY)).thenReturn(cipher);

        // When
        LocalDateTime result = spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING);

        // Then
        verify(spiedLocalDateTimeCryptoConverter).callCipherDoFinal(cipher, LOCAL_DATE_TIME_TO_CIPHER_AS_STRING.getBytes());
        assertThat(result).isEqualTo(LOCAL_DATE_TIME_TO_CIPHER);
    }

    @Test
    public void convertToEntityAttribute_should_return_unchanged_local_date_time_when_database_encryption_key_is_null() throws Exception {
        // Given
        KeyProperty.DATABASE_ENCRYPTION_KEY = null;

        // When
        LocalDateTime result = spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_CIPHER_AS_STRING);

        // Then
        assertThat(result).isEqualTo(LOCAL_DATE_TIME_TO_CIPHER);
    }

    @Test
    public void convertToEntityAttribute_should_return_unchanged_local_date_time_when_database_encryption_key_is_empty() throws Exception {
        // Given
        KeyProperty.DATABASE_ENCRYPTION_KEY = "";

        // When
        LocalDateTime result = spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_CIPHER_AS_STRING);

        // Then
        assertThat(result).isEqualTo(LOCAL_DATE_TIME_TO_CIPHER);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_cipher_initialization_fails_with_InvalidKeyException() throws Exception {
        // Given
        InvalidKeyException invalidKeyException = new InvalidKeyException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(invalidKeyException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(invalidKeyException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_cipher_initialization_fails_with_NoSuchAlgorithmException() throws Exception {
        // Given
        NoSuchAlgorithmException noSuchAlgorithmException = new NoSuchAlgorithmException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(noSuchAlgorithmException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(noSuchAlgorithmException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_cipher_initialization_fails_with_NoSuchPaddingException() throws Exception {
        // Given
        NoSuchPaddingException noSuchPaddingException = new NoSuchPaddingException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(noSuchPaddingException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(noSuchPaddingException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_cipher_initialization_fails_with_InvalidAlgorithmParameterException() throws Exception {
        // Given
        InvalidAlgorithmParameterException invalidAlgorithmParameterException = new InvalidAlgorithmParameterException();
        when(cipherInitializer.prepareAndInitCipher(anyInt(), anyString())).thenThrow(invalidAlgorithmParameterException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(invalidAlgorithmParameterException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_decryption_fails_with_BadPaddingException() throws Exception {
        // Given
        BadPaddingException badPaddingException = new BadPaddingException();
        when(spiedLocalDateTimeCryptoConverter.callCipherDoFinal(any(Cipher.class), any(byte[].class))).thenThrow(badPaddingException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(badPaddingException);
    }

    @Test
    public void convertToEntityAttribute_should_rethrow_exception_when_decryption_fails_with_IllegalBlockSizeException() throws Exception {
        // Given
        IllegalBlockSizeException illegalBlockSizeException = new IllegalBlockSizeException();
        when(spiedLocalDateTimeCryptoConverter.callCipherDoFinal(any(Cipher.class), any(byte[].class))).thenThrow(illegalBlockSizeException);

        // When
        Throwable throwable = catchThrowable(() -> spiedLocalDateTimeCryptoConverter.convertToEntityAttribute(LOCAL_DATE_TIME_TO_DECIPHER_AS_STRING));

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class).hasCause(illegalBlockSizeException);
    }

}
