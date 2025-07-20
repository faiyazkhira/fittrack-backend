package com.faiyaz.project.fittrack.dashboard.controller;

import com.faiyaz.project.fittrack.dashboard.dto.DashboardResponseDto;
import com.faiyaz.project.fittrack.dashboard.service.DashboardService;
import com.faiyaz.project.fittrack.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboard(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(dashboardService.getDashboard(user.getId()));
    }
}
