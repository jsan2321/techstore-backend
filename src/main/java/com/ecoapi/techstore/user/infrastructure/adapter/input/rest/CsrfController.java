package com.ecoapi.techstore.user.infrastructure.adapter.input.rest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Causes Spring Security to issue the Angular-readable XSRF cookie before the
 * first state-changing request. The token itself is not persisted server-side.
 */
@RestController
public class CsrfController {

    @GetMapping("${api.prefix}/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
