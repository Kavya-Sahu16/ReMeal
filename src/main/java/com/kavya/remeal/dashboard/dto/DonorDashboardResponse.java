package com.kavya.remeal.dashboard.dto;

public record DonorDashboardResponse(
        long totalDonations,
        long availableDonations,
        long pendingRequests,
        long completedDeliveries
) {}