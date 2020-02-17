# spring-data-jpa-encryption-example

[![Build Status](https://travis-ci.org/damienbeaufils/spring-data-jpa-encryption-example.svg?branch=master)](https://travis-ci.org/damienbeaufils/spring-data-jpa-encryption-example)

An example of how to encrypt and decrypt entity fields with JPA converters and Spring Data JPA.
See [blog post](https://damienbeaufils.dev/blog/how-to-properly-encrypt-data-using-jpa-converters-and-spring-data-jpa/).

## Requirements

Java 11


## How is encryption enabled

### Entity

There is a `User` entity which have different fields: `id`, `firstName`, `lastName`, `email`, `birthDate` and `creationDate`.

All fields except `id` are encrypted in database using AES algorithm.

### Repository

There is a simple `UserRepository` which extends Spring Data `JpaRepository`.

### Converters

Encryption is enabled on fields using different JPA converters: `StringCryptoConverter`, `LocalDateCryptoConverter` and `LocalDateTimeCryptoConverter`. 
This is verified with `UserRepositoryTest` integration test.

All converters are unit tested.

### Encryption key

Encryption key is empty by default (see `example.database.encryption.key` configuration key in `application.yml`).
 
You have to provide an encryption key in configuration or specify it in options when running application.


## Run tests

```
./gradlew check
```
