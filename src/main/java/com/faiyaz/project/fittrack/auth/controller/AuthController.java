package com.faiyaz.project.fittrack.auth.controller;

import com.faiyaz.project.fittrack.auth.entity.RefreshToken;
import com.faiyaz.project.fittrack.auth.jwt.JwtService;
import com.faiyaz.project.fittrack.auth.service.AuthService;
import com.faiyaz.project.fittrack.auth.service.RefreshTokenService;
import com.faiyaz.project.fittrack.user.dto.AuthResponse;
import com.faiyaz.project.fittrack.user.dto.LoginRequest;
import com.faiyaz.project.fittrack.user.dto.SignUpRequest;
import com.faiyaz.project.fittrack.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignUpRequest request,
                                                 HttpServletResponse response){
        User user = authService.register(request);
        return createAuthResponse(user, response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response){
        User user = authService.login(request);
        return createAuthResponse(user, response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshTokenValue,
                                                     HttpServletResponse response){

        if(refreshTokenValue == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token missing");
        }

        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.verifyRefreshToken(refreshTokenValue);
        if(refreshTokenOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        User user = refreshTokenOpt.get().getUser();
        String newAccessToken = jwtService.generateToken(user, 1000 * 60 * 15);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(newRefreshToken.getToken());

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User user, HttpServletResponse response){
        refreshTokenService.revokeUserTokens(user);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<AuthResponse> createAuthResponse(User user, HttpServletResponse response){
        String accessToken = jwtService.generateToken(user, 1000 * 60 * 15);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(refreshToken.getToken());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok(new AuthResponse(accessToken));
    }

    private ResponseCookie buildRefreshTokenCookie(String token){
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }
}
