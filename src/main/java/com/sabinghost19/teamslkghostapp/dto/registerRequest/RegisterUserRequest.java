package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import com.sabinghost19.teamslkghostapp.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.AssertTrue;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "Institution is required")
    private String institution;

    private String studyLevel;

    private String specialization;

    private Integer year;

    private String group;

    private String bio;

    private MultipartFile profileImage;

    private String phoneNumber;

    private NotificationPreferencesDto notificationPreferences;

    @AssertTrue(message = "Terms must be accepted")
    private boolean termsAccepted;

    @AssertTrue(message = "Privacy policy must be accepted")
    private boolean privacyPolicyAccepted;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationPreferencesDto {
        private boolean email = true;
        private boolean push = true;
        private boolean desktop = true;
    }
}


