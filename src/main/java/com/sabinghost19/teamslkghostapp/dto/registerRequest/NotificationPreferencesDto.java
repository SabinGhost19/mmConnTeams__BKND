package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesDto {
    private boolean email = true;
    private boolean push = true;
    private boolean desktop = true;

    public NotificationPreferencesDto(String jsonValue) {
        if (jsonValue != null && !jsonValue.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                NotificationPreferencesDto dto = mapper.readValue(jsonValue, NotificationPreferencesDto.class);
                this.email = dto.isEmail();
                this.push = dto.isPush();
                this.desktop = dto.isDesktop();
            } catch (Exception e) {
            }
        }
    }
}
