package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sabinghost19.teamslkghostapp.enums.Role;
import com.sabinghost19.teamslkghostapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude câmpurile null din JSON
public class RegisterUserResponseDTO {
    private boolean success;
    private String message;
    private UserDto user;
}


