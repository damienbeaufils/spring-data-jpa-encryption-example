package com.example.spring.data.jpa.encryption.domain;

import com.example.spring.data.jpa.encryption.converters.KeyProperty;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

final class EncryptionHelper {

    private EncryptionHelper() {
    }

    static void enableDatabaseEncryption(TestEntityManager testEntityManager) {
        KeyProperty.DATABASE_ENCRYPTION_KEY = "MySuperSecretKey";
        testEntityManager.clear();
    }

    static void disableDatabaseEncryption(TestEntityManager testEntityManager) {
        KeyProperty.DATABASE_ENCRYPTION_KEY = null;
        testEntityManager.clear();
    }
}
