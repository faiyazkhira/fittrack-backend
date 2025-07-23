package com.faiyaz.project.fittrack.auth.service;

import com.faiyaz.project.fittrack.auth.entity.RefreshToken;
import com.faiyaz.project.fittrack.auth.repository.RefreshTokenRepository;
import com.faiyaz.project.fittrack.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.refresh-token.expiry-days:7}")
    private long refreshTokenExpiryDays;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user){
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusSeconds(60 * 60 * 24 * refreshTokenExpiryDays))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> verifyRefreshToken(String token){
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()));
    }

    @Transactional
    public void revokeUserTokens(User user){
        refreshTokenRepository.deleteByUser(user);
    }
}
