package com.poly.livre.backend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationFinishRequest {

    private String id;
    private String rawId;
    private String type;
    private AuthenticatorResponse response;
    private String clientExtensionResults;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticatorResponse {
        private String attestationObject;
        private String clientDataJSON;
        private String[] transports;
    }
    
}
