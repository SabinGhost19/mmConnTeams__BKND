package com.sabinghost19.teamslkghostapp.dto.registerRequest.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterUserResponseDTO {
    private boolean success;
    private String message;
    private UserDto user;
}


