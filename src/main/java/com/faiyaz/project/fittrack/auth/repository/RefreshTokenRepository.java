package com.faiyaz.project.fittrack.auth.repository;

import com.faiyaz.project.fittrack.auth.entity.RefreshToken;
import com.faiyaz.project.fittrack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
