package com.ecoapi.techstore.user.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.in.*;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;
import com.ecoapi.techstore.user.domain.model.RefreshToken;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.model.UserStatus;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerHttpTest {
    private MockMvc mvc;
    private LoginUseCase login;

    @BeforeEach
    void setUp() {
        RegisterUserUseCase register = Mockito.mock(RegisterUserUseCase.class);
        login = Mockito.mock(LoginUseCase.class);
        GoogleLoginUseCase google = Mockito.mock(GoogleLoginUseCase.class);
        LogoutUseCase logout = Mockito.mock(LogoutUseCase.class);
        RefreshTokenUseCase refresh = Mockito.mock(RefreshTokenUseCase.class);
        ConfirmEmailUseCase confirm = Mockito.mock(ConfirmEmailUseCase.class);
        ResendEmailConfirmationUseCase resend = Mockito.mock(ResendEmailConfirmationUseCase.class);
        RequestPasswordResetUseCase forgot = Mockito.mock(RequestPasswordResetUseCase.class);
        ResetPasswordUseCase reset = Mockito.mock(ResetPasswordUseCase.class);
        AuthController controller = new AuthController(register, login, google, logout, refresh, confirm, resend, forgot, reset);
        ReflectionTestUtils.setField(controller, "refreshCookieName", "techstore_refresh");
        ReflectionTestUtils.setField(controller, "refreshCookieSecure", true);
        ReflectionTestUtils.setField(controller, "refreshCookieSameSite", "Lax");
        ReflectionTestUtils.setField(controller, "refreshCookiePath", "/api/v1/auth");
        ReflectionTestUtils.setField(controller, "refreshTokenExpirationDays", 7L);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .addPlaceholderValue("api.prefix", "/api/v1")
                .build();
    }

    @Test
    void loginSetsAnHttpOnlyRefreshCookieAndNeverReturnsItInJson() throws Exception {
        User user = User.reconstitute(UserId.of(7L), "Test", "Customer", new Email("customer@techstore.test"),
                "hash", null, Set.of(), null, UserStatus.ACTIVE, true, null);
        RefreshToken refreshToken = RefreshToken.create(user.getId(), 7);
        Mockito.when(login.execute(Mockito.any())).thenReturn(new AuthenticationResult(user, "access-token", refreshToken));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"customer@techstore.test\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("techstore_refresh="),
                        org.hamcrest.Matchers.containsString("HttpOnly"),
                        org.hamcrest.Matchers.containsString("Secure"),
                        org.hamcrest.Matchers.containsString("SameSite=Lax"),
                        org.hamcrest.Matchers.containsString("Path=/api/v1/auth"))))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
    }
}
