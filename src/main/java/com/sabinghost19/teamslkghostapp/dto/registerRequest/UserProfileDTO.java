package com.sabinghost19.teamslkghostapp.dto.registerRequest;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    private UUID id;

    private UUID userId;

    private String institution;

    private String studyLevel;

    private String specialization;

    private Integer year;

    private String group;

    private String bio;

    private String profileImageUrl;

    private String phoneNumber;

    private boolean termsAccepted;

    private boolean privacyPolicyAccepted;

    private Instant createdAt;

    private Instant updatedAt;
}