package com.ecoapi.techstore.user.application.port.out;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Output port to query order data needed by User use cases.
 */
public interface OrderLookupPort {

    boolean hasOrders(UserId userId);
}
