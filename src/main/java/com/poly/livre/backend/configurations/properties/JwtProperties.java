package com.poly.livre.backend.configurations.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Value
@Validated
@ConfigurationProperties("jwt")
public class JwtProperties {

    @NotEmpty
    String issuer;

    @NotEmpty
    String publicKey;

    @NotEmpty
    String algorithm;

    @NotNull
    @Min(0)
    Integer validity;

    @NotEmpty
    String privateKey;

}