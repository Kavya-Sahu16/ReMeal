package com.kavya.remeal.dashboard.dto;

public record VolunteerDashboardResponse(
        long totalAssignments,
        long acceptedAssignments,
        long pickedUpDeliveries,
        long completedDeliveries
) {}