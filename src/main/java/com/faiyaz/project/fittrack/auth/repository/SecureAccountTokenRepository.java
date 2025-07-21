package com.faiyaz.project.fittrack.auth.repository;

import com.faiyaz.project.fittrack.auth.entity.SecureAccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecureAccountTokenRepository extends JpaRepository<SecureAccountToken, UUID> {
    Optional<SecureAccountToken> findByToken(String token);
}
