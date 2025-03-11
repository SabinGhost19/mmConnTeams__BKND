package com.sabinghost19.teamslkghostapp.model;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.NotificationPreferencesDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "notification_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email_notifications", nullable = false)
    private boolean email = true;

    @Column(name = "push_notifications", nullable = false)
    private boolean push = true;

    @Column(name = "desktop_notifications", nullable = false)
    private boolean desktop = true;

    // Timestamp fields for audit
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
