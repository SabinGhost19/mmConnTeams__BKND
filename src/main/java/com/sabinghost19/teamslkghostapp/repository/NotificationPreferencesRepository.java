package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, UUID> {
}
