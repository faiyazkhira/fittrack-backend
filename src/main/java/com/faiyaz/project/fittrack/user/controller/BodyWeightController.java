package com.faiyaz.project.fittrack.user.controller;

import com.faiyaz.project.fittrack.user.dto.BodyWeightRequestDto;
import com.faiyaz.project.fittrack.user.dto.BodyWeightResponseDto;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.service.BodyWeightService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bodyweight")
public class BodyWeightController {

    private final BodyWeightService bodyWeightService;

    public BodyWeightController(BodyWeightService bodyWeightService) {
        this.bodyWeightService = bodyWeightService;
    }

    @PostMapping
    public ResponseEntity<BodyWeightResponseDto> logWeight(@AuthenticationPrincipal User user,
    @Valid @RequestBody BodyWeightRequestDto request){
        return ResponseEntity.ok(bodyWeightService.logWeight(user.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<BodyWeightResponseDto>> getWeights(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(bodyWeightService.getWeightLogs(user.getId(), startDate, endDate));
    }
}
