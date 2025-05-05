package com.sabinghost19.teamslkghostapp.dto.registerRequest.response;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import com.sabinghost19.teamslkghostapp.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private List<Role> roles;
}
