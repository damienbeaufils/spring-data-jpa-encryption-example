package com.example.spring.data.jpa.encryption.converters;

import javax.persistence.Converter;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Converter
public class LocalDateTimeCryptoConverter extends AbstractCryptoConverter<LocalDateTime> {

    public LocalDateTimeCryptoConverter() {
        this(new CipherInitializer());
    }

    public LocalDateTimeCryptoConverter(CipherInitializer cipherInitializer) {
        super(cipherInitializer);
    }

    @Override
    boolean isNotNullOrEmpty(LocalDateTime attribute) {
        return attribute != null;
    }

    @Override
    LocalDateTime stringToEntityAttribute(String dbData) {
        return isEmpty(dbData) ? null : LocalDateTime.parse(dbData, ISO_DATE_TIME);
    }

    @Override
    String entityAttributeToString(LocalDateTime attribute) {
        return attribute == null ? null : attribute.format(ISO_DATE_TIME);
    }

}
