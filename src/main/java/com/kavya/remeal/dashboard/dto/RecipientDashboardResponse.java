package com.kavya.remeal.dashboard.dto;

public record RecipientDashboardResponse(
        long totalRequests,
        long pendingRequests,
        long approvedRequests,
        long completedRequests
) {}