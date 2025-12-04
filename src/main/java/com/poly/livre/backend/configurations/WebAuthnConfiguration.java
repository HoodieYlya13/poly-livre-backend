package com.poly.livre.backend.configurations;

import com.fasterxml.jackson.databind.Module;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.converter.jackson.WebAuthnJSONModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebAuthnConfiguration {

    @Bean
    public WebAuthnManager webAuthnManager() {
        return WebAuthnManager.createNonStrictWebAuthnManager();
    }

    @Bean
    public Module webAuthnJSONModule() {
        return new WebAuthnJSONModule(new ObjectConverter());
    }

}