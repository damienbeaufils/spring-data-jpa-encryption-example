package com.example.spring.data.jpa.encryption.converters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CipherInitializerTest {

    private static final int CIPHER_BLOCK_SIZE = 16;

    private CipherInitializer spiedCipherInitializer;

    private String key;
    private int encryptionMode;

    @Before
    public void setUp() throws Exception {
        spiedCipherInitializer = spy(new CipherInitializer());
        doNothing().when(spiedCipherInitializer).callCipherInit(any(Cipher.class), anyInt(), any(Key.class), any(AlgorithmParameterSpec.class));
        doReturn(CIPHER_BLOCK_SIZE).when(spiedCipherInitializer).getCipherBlockSize(any(Cipher.class));

        key = "MySuperSecretKey";
        encryptionMode = Cipher.ENCRYPT_MODE;
    }

    @Test
    public void prepareAndInitCipher_should_configure_cipher_with_AES_algorithm_and_CBC_mode() throws Exception {
        // When
        Cipher cipher = spiedCipherInitializer.prepareAndInitCipher(encryptionMode, key);

        // Then
        assertThat(cipher.getAlgorithm()).isEqualTo("AES/CBC/PKCS5Padding");
    }

    @Test
    public void prepareAndInitCipher_should_init_cipher_with_given_encryption_mode() throws Exception {
        // When
        spiedCipherInitializer.prepareAndInitCipher(encryptionMode, key);

        // Then
        verify(spiedCipherInitializer).callCipherInit(any(Cipher.class), eq(encryptionMode), any(Key.class), any(AlgorithmParameterSpec.class));
    }

    @Test
    public void prepareAndInitCipher_should_init_cipher_with_given_key_as_bytes_array_and_AES_algorithm() throws Exception {
        // When
        spiedCipherInitializer.prepareAndInitCipher(encryptionMode, key);

        // Then
        ArgumentCaptor<Key> keyArgumentCaptor = ArgumentCaptor.forClass(Key.class);
        verify(spiedCipherInitializer).callCipherInit(any(Cipher.class), anyInt(), keyArgumentCaptor.capture(), any(AlgorithmParameterSpec.class));
        Key value = keyArgumentCaptor.getValue();
        assertThat(value.getAlgorithm()).isEqualTo("AES");
        assertThat(new String(value.getEncoded())).isEqualTo(key);
    }

    @Test
    public void prepareAndInitCipher_should_init_cipher_with_algorithm_parameter_spec_using_cipher_block_size() throws Exception {
        // When
        spiedCipherInitializer.prepareAndInitCipher(encryptionMode, key);

        // Then
        ArgumentCaptor<AlgorithmParameterSpec> algorithmParameterSpecArgumentCaptor = ArgumentCaptor.forClass(AlgorithmParameterSpec.class);
        verify(spiedCipherInitializer).callCipherInit(any(Cipher.class), anyInt(), any(Key.class), algorithmParameterSpecArgumentCaptor.capture());
        AlgorithmParameterSpec value = algorithmParameterSpecArgumentCaptor.getValue();
        IvParameterSpec ivParameterSpec = (IvParameterSpec) value;
        assertThat(ivParameterSpec.getIV()).hasSize(CIPHER_BLOCK_SIZE);
    }

}
