package com.sabinghost19.teamslkghostapp.dto.registerRequest.descentralizers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.NotificationPreferencesDto;

import java.io.IOException;

public class NotificationPreferencesDeserializer extends JsonDeserializer<NotificationPreferencesDto> {
    @Override
    public NotificationPreferencesDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return new NotificationPreferencesDto();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(value, NotificationPreferencesDto.class);
        } catch (Exception e) {
            return new NotificationPreferencesDto();
        }
    }
}