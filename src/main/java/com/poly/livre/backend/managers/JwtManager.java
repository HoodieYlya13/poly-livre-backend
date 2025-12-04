package com.poly.livre.backend.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.livre.backend.configurations.properties.JwtProperties;
import com.poly.livre.backend.exceptions.InternalServerException;
import com.poly.livre.backend.exceptions.errors.JwtErrorCode;
import com.poly.livre.backend.exceptions.errors.SystemErrorCode;
import com.poly.livre.backend.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class JwtManager {

    private final Clock clock;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    private void setupKey() {
        try {
            this.privateKey = this.parsePrivateKey(jwtProperties.getPrivateKey(), jwtProperties.getAlgorithm());
            this.publicKey = this.parsePublicKey(jwtProperties.getPublicKey(), jwtProperties.getAlgorithm());
        } catch (InternalServerException e) {
            log.error("Error while constructing private/public key from configuration: {}", e.getMessage());
            throw new InternalServerException(e, SystemErrorCode.TECHNICAL_ERROR);
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        return buildToken(extraClaims, user.getEmail(), jwtProperties.getValidity());
    }

    public Integer getExpirationTime() {
        return jwtProperties.getValidity();
    }

    public String generateChallengeToken(String challenge) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("challenge", challenge);
        return buildToken(claims, "challenge-token", 300); // 5 minutes validity
    }

    public String extractChallenge(String token) {
        return extractClaim(token, claims -> claims.get("challenge", String.class));
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String subject,
            long expiration) {

        Instant now = clock.instant();

        JwtBuilder builder = Jwts
                .builder()
                .subject(subject)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiration)));

        extraClaims.forEach(builder::claim);

        return builder
                .signWith(privateKey)
                .compact();
    }

    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(publicKey)
                .requireIssuer(jwtProperties.getIssuer())
                .json(new JacksonDeserializer<>(objectMapper))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PrivateKey parsePrivateKey(String privateKey, String algorithm) {
        try {
            byte[] privateKeyB = Base64.getDecoder().decode(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyB));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalServerException(e, JwtErrorCode.PRIVATE_KEY_INVALID);
        }
    }

    private PublicKey parsePublicKey(String publicKey, String algorithm) {
        try {
            byte[] publicKeyB = Base64.getDecoder().decode(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyB));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalServerException(e, JwtErrorCode.PUBLIC_KEY_INVALID);
        }
    }

}