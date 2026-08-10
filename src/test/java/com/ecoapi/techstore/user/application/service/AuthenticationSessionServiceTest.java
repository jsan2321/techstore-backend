package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.application.port.out.TokenProviderPort;
import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.out.AuthenticationPort;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.LoginCommand;
import com.ecoapi.techstore.user.application.service.dto.LogoutCommand;
import com.ecoapi.techstore.user.application.service.dto.RefreshTokenCommand;
import com.ecoapi.techstore.user.domain.exception.EmailNotVerifiedException;
import com.ecoapi.techstore.user.domain.exception.InvalidRefreshTokenException;
import com.ecoapi.techstore.user.domain.model.RefreshToken;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.model.UserStatus;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationSessionServiceTest {

    @Test
    void refreshesAnActiveUsersAccessTokenWithoutChangingTheRefreshToken() {
        RefreshTokenRepositoryPort refreshTokens = mock(RefreshTokenRepositoryPort.class);
        UserRepositoryPort users = mock(UserRepositoryPort.class);
        TokenProviderPort tokenProvider = mock(TokenProviderPort.class);
        User user = activeVerifiedUser();
        RefreshToken refreshToken = RefreshToken.create(user.getId(), 7);

        when(refreshTokens.findByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));
        when(users.findById(user.getId())).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken(eq("42"), eq("customer@techstore.test"), anyList()))
                .thenReturn("new-access-token");

        var result = new RefreshTokenService(refreshTokens, users, tokenProvider)
                .refreshAccessToken(new RefreshTokenCommand(refreshToken.getToken()));

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isSameAs(refreshToken);
        verify(tokenProvider).generateToken(eq("42"), eq("customer@techstore.test"), anyList());
    }

    @Test
    void rejectsUnknownRefreshTokensBeforeLookingUpAUser() {
        RefreshTokenRepositoryPort refreshTokens = mock(RefreshTokenRepositoryPort.class);
        UserRepositoryPort users = mock(UserRepositoryPort.class);
        TokenProviderPort tokenProvider = mock(TokenProviderPort.class);
        when(refreshTokens.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new RefreshTokenService(refreshTokens, users, tokenProvider)
                .refreshAccessToken(new RefreshTokenCommand("missing")))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(users, never()).findById(any());
        verify(tokenProvider, never()).generateToken(any(), any(), anyList());
    }

    @Test
    void refusesLoginForAnUnverifiedAccountAndDoesNotCreateASession() {
        AuthenticationPort authentication = mock(AuthenticationPort.class);
        TokenProviderPort tokenProvider = mock(TokenProviderPort.class);
        RefreshTokenRepositoryPort refreshTokens = mock(RefreshTokenRepositoryPort.class);
        User unverified = User.reconstitute(UserId.of(42L), "Customer", "Test",
                new Email("customer@techstore.test"), "password-hash", null, Set.of(), null,
                UserStatus.ACTIVE, false, null);
        when(authentication.authenticate("customer@techstore.test", "password")).thenReturn(unverified);

        assertThatThrownBy(() -> new LoginUserService(authentication, tokenProvider, refreshTokens, 7)
                .execute(new LoginCommand("customer@techstore.test", "password")))
                .isInstanceOf(EmailNotVerifiedException.class);

        verify(tokenProvider, never()).generateToken(any(), any(), anyList());
        verify(refreshTokens, never()).save(any());
    }

    @Test
    void logoutDeletesOnlyTheCurrentRefreshToken() {
        RefreshTokenRepositoryPort refreshTokens = mock(RefreshTokenRepositoryPort.class);

        new LogoutService(refreshTokens).logout(new LogoutCommand(UserId.of(42L), "current-refresh-token"));

        verify(refreshTokens).deleteByToken("current-refresh-token");
        verify(refreshTokens, never()).revokeAllByUserId(any());
    }

    private User activeVerifiedUser() {
        return User.reconstitute(UserId.of(42L), "Customer", "Test", new Email("customer@techstore.test"),
                "password-hash", null, Set.of(), null, UserStatus.ACTIVE, true, null);
    }
}
