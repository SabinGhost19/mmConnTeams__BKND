package com.sabinghost19.teamslkghostapp.dto.registerRequest.response;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserReponseDTO {
    private boolean success;
    private String message;
    private UserDto user;
    private String token;
    private String refreshToken;
}
