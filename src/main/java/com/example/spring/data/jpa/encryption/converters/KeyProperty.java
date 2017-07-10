package com.example.spring.data.jpa.encryption.converters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyProperty {

    public static String DATABASE_ENCRYPTION_KEY;

    @Value("${example.database.encryption.key}")
    public void setDatabase(String databaseEncryptionKey) {
        DATABASE_ENCRYPTION_KEY = databaseEncryptionKey;
    }

}
