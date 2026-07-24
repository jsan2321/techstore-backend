package com.ecoapi.techstore.order.application.port.out.dto;

/**
 * User summary data for admin order views.
 */
public record UserSummaryData(
        Long userId,
        String firstName,
        String lastName,
        String email,
        String status,
        boolean emailVerified
) {
}
