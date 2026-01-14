package com.poly.livre.backend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthenticationRequests {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MagicLinkRequest {
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MagicLinkVerifyRequest {
        private String token;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PasskeyRegisterStartRequest {
        private String email;
        private String passkeyName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RenamePasskeyRequest {
        private String passkeyName;
    }

}
