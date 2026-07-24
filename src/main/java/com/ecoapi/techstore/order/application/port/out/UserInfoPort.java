package com.ecoapi.techstore.order.application.port.out;

import com.ecoapi.techstore.order.application.port.out.dto.UserSummaryData;

import java.util.Optional;

/**
 * Output port for fetching user summary data used in admin order responses.
 */
public interface UserInfoPort {

    Optional<UserSummaryData> getUserSummary(Long userId);
}
