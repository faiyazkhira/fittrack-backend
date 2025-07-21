package com.faiyaz.project.fittrack.auth.controller;

import com.faiyaz.project.fittrack.auth.dto.SecureAccountRequest;
import com.faiyaz.project.fittrack.auth.service.SecureAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class SecureAccountController {

    private final SecureAccountService service;

    public SecureAccountController(SecureAccountService service) {
        this.service = service;
    }

    @PostMapping("/secure-account")
    public ResponseEntity<String> secureAccount(@Valid @RequestBody SecureAccountRequest request){
        service.secureAccount(request.getToken());
        return ResponseEntity.ok("Account secured and password reset email sent.");
    }
}
