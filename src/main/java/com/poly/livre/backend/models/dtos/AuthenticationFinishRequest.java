package com.poly.livre.backend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationFinishRequest {

    private String id;
    private String rawId;
    private String type;
    private AuthenticatorResponse response;
    private Map<String, Object> clientExtensionResults;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticatorResponse {
        private String authenticatorData;
        private String clientDataJSON;
        private String signature;
        private String userHandle;
    }
    
}
