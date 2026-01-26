package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;

/**
 * Input Port (Use Case) for retrieving user profile
 */
public interface GetUserProfileUseCase {
    
    User execute(UserId userId);
}
