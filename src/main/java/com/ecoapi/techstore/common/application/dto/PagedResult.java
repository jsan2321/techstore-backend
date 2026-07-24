package com.ecoapi.techstore.common.application.dto;

import java.util.List;

/**
 * Generic wrapper for paginated results
 * Used across all bounded contexts for consistent pagination
 */
public record PagedResult<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static <T> PagedResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        return new PagedResult<>(content, page, size, totalElements, totalPages, first, last);
    }
    
    public static <T> PagedResult<T> empty(int page, int size) {
        return new PagedResult<>(List.of(), page, size, 0, 0, true, true);
    }
}