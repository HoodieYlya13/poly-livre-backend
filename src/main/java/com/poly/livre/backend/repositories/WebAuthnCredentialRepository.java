package com.poly.livre.backend.repositories;

import com.poly.livre.backend.models.entities.WebAuthnCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WebAuthnCredentialRepository extends JpaRepository<WebAuthnCredential, UUID> {

    Optional<WebAuthnCredential> findByCredentialId(String credentialId);

    List<WebAuthnCredential> findAllByUserId(UUID userId);

}