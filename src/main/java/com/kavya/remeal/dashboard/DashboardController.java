package com.kavya.remeal.dashboard;

import com.kavya.remeal.dashboard.dto.*;
import com.kavya.remeal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/donor")
    public DonorDashboardResponse getDonorDashboard(
            @AuthenticationPrincipal User user) {

        return dashboardService.getDonorDashboard(user);
    }

    @GetMapping("/recipient")
    public RecipientDashboardResponse getRecipientDashboard(
            @AuthenticationPrincipal User user) {

        return dashboardService.getRecipientDashboard(user);
    }

    @GetMapping("/volunteer")
    public VolunteerDashboardResponse getVolunteerDashboard(
            @AuthenticationPrincipal User user) {

        return dashboardService.getVolunteerDashboard(user);
    }
}