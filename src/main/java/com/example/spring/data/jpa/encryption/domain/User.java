package com.example.spring.data.jpa.encryption.domain;

import com.example.spring.data.jpa.encryption.converters.LocalDateCryptoConverter;
import com.example.spring.data.jpa.encryption.converters.LocalDateTimeCryptoConverter;
import com.example.spring.data.jpa.encryption.converters.StringCryptoConverter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    @Convert(converter = StringCryptoConverter.class)
    private String firstName;

    @Column(name = "last_name")
    @Convert(converter = StringCryptoConverter.class)
    private String lastName;

    @Column(name = "email")
    @Convert(converter = StringCryptoConverter.class)
    private String email;

    @Column(name = "birth_date")
    @Convert(converter = LocalDateCryptoConverter.class)
    private LocalDate birthDate;

    @Column(name = "creation_date")
    @Convert(converter = LocalDateTimeCryptoConverter.class)
    private LocalDateTime creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
