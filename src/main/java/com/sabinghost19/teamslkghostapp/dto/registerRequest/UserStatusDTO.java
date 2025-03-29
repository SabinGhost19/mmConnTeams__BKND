package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import com.sabinghost19.teamslkghostapp.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {
    private UUID userId;
    private String status;
}