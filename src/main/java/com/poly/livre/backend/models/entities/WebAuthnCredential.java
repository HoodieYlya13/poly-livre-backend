package com.poly.livre.backend.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "WEBAUTHN_CREDENTIALS")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WebAuthnCredential extends AuditDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "CREDENTIAL_ID", columnDefinition = "TEXT", nullable = false)
    private String credentialId;

    @Column(name = "PUBLIC_KEY", columnDefinition = "TEXT", nullable = false)
    private String publicKey;

    @Column(name = "SIGN_COUNT", columnDefinition = "BIGINT", nullable = false)
    private long signCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

}