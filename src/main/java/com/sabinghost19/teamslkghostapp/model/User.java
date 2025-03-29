package com.sabinghost19.teamslkghostapp.model;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.convertors.PostgreSQLEnumType;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.convertors.StatusConverter;
import com.sabinghost19.teamslkghostapp.enums.Role;
import com.sabinghost19.teamslkghostapp.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name ="password_hash",nullable = false)
    private String password;

    @Column(name = "status")
    private String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.ORDINAL)
    private List<Role> roles = new ArrayList<>();


    public Status getStatusAsEnum() {
        try {
            return Status.valueOf(this.status);
        } catch (IllegalArgumentException e) {
            return Status.OFFLINE; // valoare implicită
        }
    }

    public void setStatusAsEnum(Status status) {
        this.status = status.name();
    }

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ToString.Exclude // Exclude explicit (alternativ la @ToString(exclude))
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Include
    private UserProfile profile;


    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}