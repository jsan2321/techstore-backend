package com.ecoapi.techstore.user.infrastructure.adapter.output.security;

import com.ecoapi.techstore.user.application.port.out.GoogleIdentityProviderPort;
import com.ecoapi.techstore.user.application.service.dto.GoogleIdentityProfile;
import com.ecoapi.techstore.user.domain.exception.InvalidGoogleIdTokenException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Verifies Google ID tokens using Google's tokeninfo endpoint.
 */
public class GoogleIdentityProviderAdapter implements GoogleIdentityProviderPort {

    private static final Set<String> VALID_ISSUERS = Set.of("accounts.google.com", "https://accounts.google.com");

    private final RestClient restClient;
    private final String clientId;

    public GoogleIdentityProviderAdapter(String clientId, String tokenInfoBaseUrl) {
        this.clientId = Objects.requireNonNull(clientId, "Google client id is required").trim();
        String baseUrl = Objects.requireNonNull(trimTrailingSlash(tokenInfoBaseUrl));
        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
                .build();
    }

    @Override
    public GoogleIdentityProfile verifyIdToken(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new InvalidGoogleIdTokenException("Google ID token is required");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/tokeninfo")
                            .queryParam("id_token", idToken)
                            .build())
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.isEmpty()) {
                throw new InvalidGoogleIdTokenException("Empty response from Google token verification");
            }

            String audience = requiredString(response, "aud");
            if (!clientId.equals(audience)) {
                throw new InvalidGoogleIdTokenException("Google token audience does not match configured client");
            }

            String issuer = requiredString(response, "iss");
            if (!VALID_ISSUERS.contains(issuer)) {
                throw new InvalidGoogleIdTokenException("Google token issuer is invalid");
            }

            String email = requiredString(response, "email");
            boolean emailVerified = Boolean.parseBoolean(requiredString(response, "email_verified"));
            if (!emailVerified) {
                throw new InvalidGoogleIdTokenException("Google account email is not verified");
            }

            long expiryEpochSeconds = Long.parseLong(requiredString(response, "exp"));
            if (Instant.now().isAfter(Instant.ofEpochSecond(expiryEpochSeconds))) {
                throw new InvalidGoogleIdTokenException("Google ID token is expired");
            }

            return new GoogleIdentityProfile(
                    requiredString(response, "sub"),
                    email,
                    optionalString(response, "given_name"),
                    optionalString(response, "family_name"),
                    optionalString(response, "name")
            );
        } catch (RestClientResponseException ex) {
            throw new InvalidGoogleIdTokenException("Invalid Google ID token", ex);
        } catch (InvalidGoogleIdTokenException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidGoogleIdTokenException("Failed to verify Google ID token", ex);
        }
    }

    private String requiredString(Map<String, Object> response, String key) {
        Object value = response.get(key);
        if (value == null) {
            throw new InvalidGoogleIdTokenException("Missing Google token claim: " + key);
        }
        String stringValue = String.valueOf(value).trim();
        if (stringValue.isBlank()) {
            throw new InvalidGoogleIdTokenException("Blank Google token claim: " + key);
        }
        return stringValue;
    }

    private String optionalString(Map<String, Object> response, String key) {
        Object value = response.get(key);
        if (value == null) {
            return null;
        }
        String stringValue = String.valueOf(value).trim();
        return stringValue.isBlank() ? null : stringValue;
    }

    private String trimTrailingSlash(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Google token info base URL is required");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
