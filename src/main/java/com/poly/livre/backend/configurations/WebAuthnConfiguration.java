package com.poly.livre.backend.configurations;

import com.webauthn4j.WebAuthnManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebAuthnConfiguration {

    @Bean
    public WebAuthnManager webAuthnManager() {
        return WebAuthnManager.createNonStrictWebAuthnManager();
    }

}