package com.example.spring.data.jpa.encryption.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static com.example.spring.data.jpa.encryption.domain.EncryptionHelper.disableDatabaseEncryption;
import static com.example.spring.data.jpa.encryption.domain.EncryptionHelper.enableDatabaseEncryption;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    void setUp() {
        enableDatabaseEncryption(testEntityManager);
        user = new User();
    }

    @Test
    void save_should_persist_user_with_auto_incremented_id() {
        // Given
        User firstPersist = userRepository.save(user);
        User secondUser = new User();

        // When
        User secondPersist = userRepository.save(secondUser);

        // Then
        assertThat(secondPersist.getId()).isEqualTo(firstPersist.getId() + 1);
    }

    @Test
    void save_should_verify_that_encryption_is_enabled_on_first_name_field() {
        // Given
        String plainFirstName = "plain first name";
        user.setFirstName(plainFirstName);
        User savedUserWithEncryptionEnabled = userRepository.save(user);
        disableDatabaseEncryption(testEntityManager);

        // When
        User userRetrievedWithoutEncryptionEnabled = testEntityManager.find(User.class, savedUserWithEncryptionEnabled.getId());

        // Then
        assertThat(userRetrievedWithoutEncryptionEnabled.getFirstName())
                .isNotEqualTo(plainFirstName)
                .isEqualTo("S4vRPBO8X2f2YF+YFEWWrzK5eHtRGSpYzrA7j9TI1gI=");
    }

    @Test
    void save_should_verify_that_encryption_is_enabled_on_last_name_field() {
        // Given
        String plainLastName = "plain last name";
        user.setLastName(plainLastName);
        User savedUserWithEncryptionEnabled = userRepository.save(user);
        disableDatabaseEncryption(testEntityManager);

        // When
        User userRetrievedWithoutEncryptionEnabled = testEntityManager.find(User.class, savedUserWithEncryptionEnabled.getId());

        // Then
        assertThat(userRetrievedWithoutEncryptionEnabled.getLastName())
                .isNotEqualTo(plainLastName)
                .isEqualTo("QSsxt5JpKdKnyAGYl2HLbA==");
    }

    @Test
    void save_should_verify_that_encryption_is_enabled_on_email_field() {
        // Given
        String plainEmail = "email@example.org";
        user.setEmail(plainEmail);
        User savedUserWithEncryptionEnabled = userRepository.save(user);
        disableDatabaseEncryption(testEntityManager);

        // When
        User userRetrievedWithoutEncryptionEnabled = testEntityManager.find(User.class, savedUserWithEncryptionEnabled.getId());

        // Then
        assertThat(userRetrievedWithoutEncryptionEnabled.getEmail())
                .isNotEqualTo(plainEmail)
                .isEqualTo("13DhN2Ak/USTo1UrzjNgOmowXgQ5+HdcEFtaojE5zfI=");
    }

    @Test
    void save_should_verify_that_encryption_is_enabled_on_birth_date_field() {
        // Given
        LocalDate birthDate = LocalDate.of(1988, 3, 28);
        user.setBirthDate(birthDate);
        User savedUserWithEncryptionEnabled = userRepository.save(user);
        disableDatabaseEncryption(testEntityManager);

        // When
        Throwable throwable = catchThrowable(() -> testEntityManager.find(User.class, savedUserWithEncryptionEnabled.getId()));

        // Then
        assertThat(throwable).hasCauseInstanceOf(DateTimeParseException.class)
                .hasStackTraceContaining("u/JbG4KguO6q0Eh7PjGfYw==");
    }

    @Test
    void save_should_verify_that_encryption_is_enabled_on_creation_date_field() {
        // Given
        LocalDateTime creationDate = LocalDateTime.of(2017, 7, 10, 9, 58, 17);
        user.setCreationDate(creationDate);
        User savedUserWithEncryptionEnabled = userRepository.save(user);
        disableDatabaseEncryption(testEntityManager);

        // When
        Throwable throwable = catchThrowable(() -> testEntityManager.find(User.class, savedUserWithEncryptionEnabled.getId()));

        // Then
        assertThat(throwable).hasCauseInstanceOf(DateTimeParseException.class)
                .hasStackTraceContaining("70mKrO09DnCkDbrzFf3IGXWMAMTgLwHGdLsPPqq7ZR4=");
    }

}