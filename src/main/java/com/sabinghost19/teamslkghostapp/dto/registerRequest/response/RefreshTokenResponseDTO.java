package com.sabinghost19.teamslkghostapp.dto.registerRequest.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponseDTO {
    private boolean success;
    private String message;
    private String token;
    private String refreshToken;
}
