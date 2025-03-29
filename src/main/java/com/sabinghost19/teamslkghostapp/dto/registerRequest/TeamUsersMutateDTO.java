package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import com.sabinghost19.teamslkghostapp.enums.Role;
import com.sabinghost19.teamslkghostapp.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TeamUsersMutateDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private String profileImage;
    private String department;
    private List<Role> roles;
}

