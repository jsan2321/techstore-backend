package com.ecoapi.goodshopping.user.application.port.in;

/**
 * Query for getting all users with pagination
 * @param page Page number (0-based)
 * @param size Page size
 */
public record GetAllUsersQuery(
    int page,
    int size
) {
    public GetAllUsersQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }
}
