package com.sabinghost19.teamslkghostapp.dto.registerRequest.convertors;

import com.sabinghost19.teamslkghostapp.enums.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return Status.valueOf(dbData);
        } catch (IllegalArgumentException e) {

            return Status.OFFLINE;
        }
    }
}