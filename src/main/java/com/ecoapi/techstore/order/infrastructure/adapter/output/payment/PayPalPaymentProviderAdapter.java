package com.ecoapi.techstore.order.infrastructure.adapter.output.payment;

import com.ecoapi.techstore.order.application.port.out.PaymentProviderPort;
import com.ecoapi.techstore.order.domain.exception.PaymentProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PayPalPaymentProviderAdapter implements PaymentProviderPort {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;
    private final String returnUrl;
    private final String cancelUrl;

    public PayPalPaymentProviderAdapter(ObjectMapper objectMapper,
                                        String clientId,
                                        String clientSecret,
                                        String baseUrl,
                                        String returnUrl,
                                        String cancelUrl) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper is required");
        this.clientId = Objects.requireNonNull(clientId, "PayPal client id is required");
        this.clientSecret = Objects.requireNonNull(clientSecret, "PayPal client secret is required");
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.returnUrl = Objects.requireNonNull(returnUrl, "PayPal return url is required");
        this.cancelUrl = Objects.requireNonNull(cancelUrl, "PayPal cancel url is required");
    }

    @Override
        public CreatePayPalOrderResult createPayPalOrder(Long orderId,
                                  BigDecimal amount,
                                  String currency,
                                  PayPalShippingAddress shippingAddress) {
        try {
            String token = fetchAccessToken();
            String normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

            Map<String, Object> purchaseUnit = new LinkedHashMap<>();
            purchaseUnit.put("custom_id", String.valueOf(orderId));
            purchaseUnit.put("amount", Map.of(
                "currency_code", currency,
                "value", normalizedAmount
            ));

            if (shippingAddress != null) {
            Map<String, Object> shippingMap = new LinkedHashMap<>();
            shippingMap.put("name", Map.of(
                "full_name", truncate(Objects.requireNonNull(shippingAddress.fullName()), 300)
            ));

            Map<String, Object> shippingAddressMap = new LinkedHashMap<>();
            shippingAddressMap.put("address_line_1", truncate(Objects.requireNonNull(shippingAddress.street()), 300));
            if (hasText(shippingAddress.addressLine2())) {
                shippingAddressMap.put("address_line_2", truncate(shippingAddress.addressLine2(), 300));
            }
            shippingAddressMap.put("admin_area_2", truncate(Objects.requireNonNull(shippingAddress.city()), 120));
            shippingAddressMap.put("admin_area_1", truncate(Objects.requireNonNull(shippingAddress.state()), 120));
            shippingAddressMap.put("postal_code", truncate(Objects.requireNonNull(shippingAddress.postalCode()), 60));
            shippingAddressMap.put("country_code", normalizeCountryCode(shippingAddress.countryCode()));

            shippingMap.put("address", shippingAddressMap);
            purchaseUnit.put("shipping", shippingMap);
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("intent", "CAPTURE");
            payload.put("purchase_units", List.of(purchaseUnit));
            payload.put("application_context", Map.of(
                "return_url", returnUrl + "?orderId=" + orderId,
                "cancel_url", cancelUrl + "?orderId=" + orderId,
                "user_action", "PAY_NOW"
            ));

            String responseBody = restClient.post()
                    .uri(baseUrl + "/v2/checkout/orders")
                    .headers(headers -> {
                        headers.setBearerAuth(Objects.requireNonNull(token));
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.setAccept(Objects.requireNonNull(List.of(MediaType.APPLICATION_JSON)));
                    })
                    .body(Objects.requireNonNull(payload))
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            String providerOrderId = root.path("id").asText(null);
            String approvalUrl = findApprovalUrl(root.path("links"));

            if (providerOrderId == null || providerOrderId.isBlank()) {
                throw new PaymentProcessingException("PayPal did not return an order id");
            }
            if (approvalUrl == null || approvalUrl.isBlank()) {
                throw new PaymentProcessingException("PayPal did not return an approval url");
            }

            return new CreatePayPalOrderResult(providerOrderId, approvalUrl);
        } catch (RestClientResponseException ex) {
            throw new PaymentProcessingException("PayPal create order request failed: " + ex.getResponseBodyAsString(), ex);
        } catch (PaymentProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PaymentProcessingException("Failed to create PayPal order", ex);
        }
    }

    @Override
    public CapturePayPalOrderResult capturePayPalOrder(String providerOrderId) {
        try {
            String token = fetchAccessToken();

            String responseBody = restClient.post()
                    .uri(baseUrl + "/v2/checkout/orders/" + providerOrderId + "/capture")
                    .headers(headers -> {
                        headers.setBearerAuth(Objects.requireNonNull(token));
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.setAccept(Objects.requireNonNull(List.of(MediaType.APPLICATION_JSON)));
                    })
                    .body(Objects.requireNonNull(Map.of()))
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            String providerStatus = root.path("status").asText();
            String providerCaptureId = root.path("purchase_units")
                    .path(0)
                    .path("payments")
                    .path("captures")
                    .path(0)
                    .path("id")
                    .asText(null);

            if (providerCaptureId == null || providerCaptureId.isBlank()) {
                throw new PaymentProcessingException("PayPal did not return a capture id");
            }

            return new CapturePayPalOrderResult(providerOrderId, providerCaptureId, providerStatus);
        } catch (RestClientResponseException ex) {
            throw new PaymentProcessingException("PayPal capture request failed: " + ex.getResponseBodyAsString(), ex);
        } catch (PaymentProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PaymentProcessingException("Failed to capture PayPal order", ex);
        }
    }

    private String fetchAccessToken() {
        try {
            String responseBody = restClient.post()
                    .uri(baseUrl + "/v1/oauth2/token")
                    .headers(headers -> {
                        headers.setBasicAuth(Objects.requireNonNull(clientId), Objects.requireNonNull(clientSecret));
                        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                        headers.setAccept(Objects.requireNonNull(List.of(MediaType.APPLICATION_JSON)));
                    })
                    .body("grant_type=client_credentials")
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            String accessToken = root.path("access_token").asText(null);
            if (accessToken == null || accessToken.isBlank()) {
                throw new PaymentProcessingException("PayPal access token response was empty");
            }
            return accessToken;
        } catch (RestClientResponseException ex) {
            throw new PaymentProcessingException("PayPal auth request failed: " + ex.getResponseBodyAsString(), ex);
        } catch (PaymentProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PaymentProcessingException("Failed to authenticate against PayPal", ex);
        }
    }

    private String findApprovalUrl(JsonNode links) {
        if (links == null || !links.isArray()) {
            return null;
        }
        for (JsonNode link : links) {
            if ("approve".equalsIgnoreCase(link.path("rel").asText())) {
                return link.path("href").asText(null);
            }
        }
        return null;
    }

    private String trimTrailingSlash(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("PayPal base url is required");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String normalizeCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            throw new PaymentProcessingException("Shipping country code is required for PayPal payload");
        }
        String normalized = countryCode.trim().toUpperCase();
        if (normalized.length() != 2) {
            throw new PaymentProcessingException("Shipping country code must be 2-letter ISO code");
        }
        return normalized;
    }

    private String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
