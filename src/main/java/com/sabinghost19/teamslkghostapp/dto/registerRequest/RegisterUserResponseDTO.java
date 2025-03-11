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
    private String token;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class UserDto {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private Role role;

        public UserDto(User user) {
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.role = user.getRole();
        }
    }
}


