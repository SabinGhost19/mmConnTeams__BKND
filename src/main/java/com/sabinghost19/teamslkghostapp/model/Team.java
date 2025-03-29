package com.sabinghost19.teamslkghostapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"members", "channels"}) // Exclude colecțiile
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Folosește doar ID pentru equals/hashCode
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<Channel> channels = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamMember> members = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addMember(TeamMember member) {
        if (this.members == null) {
            this.members = new LinkedHashSet<>();
        }
        this.members.add(member);
        member.setTeam(this);
    }

    public void removeMember(TeamMember member) {
        if (this.members != null) {
            this.members.remove(member);
        }
        member.setTeam(null);
    }
}