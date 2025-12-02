package com.poly.livre.backend.services;

import com.poly.livre.backend.models.dtos.AuthenticationFinishRequest;
import com.poly.livre.backend.models.dtos.RegistrationFinishRequest;
import com.poly.livre.backend.models.entities.User;
import com.poly.livre.backend.models.entities.WebAuthnCredential;
import com.poly.livre.backend.repositories.WebAuthnCredentialRepository;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialRpEntity;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.PublicKeyCredentialUserEntity;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.UserVerificationRequirement;
import com.webauthn4j.data.attestation.authenticator.AuthenticatorData;

import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.attestation.authenticator.AAGUID;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.authenticator.COSEKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebAuthnService {

        private final WebAuthnCredentialRepository credentialRepository;
        private final WebAuthnManager webAuthnManager;

        @Value("${webauthn.rp-id}")
        private String rpId;

        @Value("${webauthn.rp-name}")
        private String rpName;

        @Value("${webauthn.origin}")
        private String origin;

        public PublicKeyCredentialCreationOptions startRegistration(User user) {
                Challenge challenge = new DefaultChallenge();

                PublicKeyCredentialUserEntity userEntity = new PublicKeyCredentialUserEntity(
                                user.getId().toString().getBytes(),
                                user.getUsername(),
                                user.getUsername());

                PublicKeyCredentialRpEntity rpEntity = new PublicKeyCredentialRpEntity(rpId, rpName);

                List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(
                                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY,
                                                COSEAlgorithmIdentifier.ES256),
                                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY,
                                                COSEAlgorithmIdentifier.RS256));

                return new PublicKeyCredentialCreationOptions(
                                rpEntity,
                                userEntity,
                                challenge,
                                pubKeyCredParams);
        }

        public PublicKeyCredentialRequestOptions startAuthentication(User user) {
                Challenge challenge = new DefaultChallenge();

                return new PublicKeyCredentialRequestOptions(
                                challenge,
                                60000L,
                                rpId,
                                null,
                                UserVerificationRequirement.PREFERRED,
                                null);
        }

        @Transactional
        public void finishRegistration(User user, RegistrationFinishRequest request) {
                RegistrationRequest registrationRequest = new RegistrationRequest(
                                request.getResponse().getAttestationObject().getBytes(),
                                request.getResponse().getClientDataJSON().getBytes());

                RegistrationParameters registrationParameters = new RegistrationParameters(
                                new ServerProperty(new Origin(origin), rpId, null, null),
                                null, // UserVerificationRequirement
                                false // userPresenceRequired
                );

                RegistrationData registrationData = webAuthnManager.parse(registrationRequest);
                webAuthnManager.verify(registrationData, registrationParameters);

                AuthenticatorData<?> authenticatorData = registrationData.getAttestationObject().getAuthenticatorData();
                byte[] credentialId = authenticatorData.getAttestedCredentialData().getCredentialId();
                COSEKey coseKey = authenticatorData.getAttestedCredentialData().getCOSEKey();

                String credentialIdString = java.util.Base64.getUrlEncoder().withoutPadding()
                                .encodeToString(credentialId);

                ObjectConverter objectConverter = new ObjectConverter();
                byte[] publicKeyBytes = objectConverter.getCborConverter().writeValueAsBytes(coseKey);
                String publicKeyString = java.util.Base64.getUrlEncoder().withoutPadding()
                                .encodeToString(publicKeyBytes);

                WebAuthnCredential credential = WebAuthnCredential.builder()
                                .credentialId(credentialIdString)
                                .user(user)
                                .publicKey(publicKeyString)
                                .signCount(authenticatorData.getSignCount())
                                .build();

                credentialRepository.save(credential);
        }

        @Transactional
        public void finishLogin(User user, AuthenticationFinishRequest request) {
                WebAuthnCredential credential = credentialRepository.findByCredentialId(request.getId())
                                .orElseThrow(() -> new RuntimeException("Credential not found"));

                AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                                request.getId().getBytes(),
                                request.getResponse().getAuthenticatorData().getBytes(),
                                request.getResponse().getClientDataJSON().getBytes(),
                                request.getResponse().getSignature().getBytes());

                ObjectConverter objectConverter = new ObjectConverter();
                byte[] publicKeyBytes = java.util.Base64.getUrlDecoder().decode(credential.getPublicKey());
                COSEKey coseKey = objectConverter.getCborConverter().readValue(publicKeyBytes, COSEKey.class);

                byte[] credentialId = java.util.Base64.getUrlDecoder().decode(credential.getCredentialId());

                AttestedCredentialData attestedCredentialData = new AttestedCredentialData(
                                new AAGUID(AAGUID.ZERO.getValue()), credentialId, coseKey);

                CredentialRecord credentialRecord = new CredentialRecordImpl(
                                null, // AttestationStatement
                                null, // uvInitialized
                                null, // backupEligible
                                null, // backupState
                                credential.getSignCount(),
                                attestedCredentialData,
                                null, // authenticationExtensionsAuthenticatorOutputs
                                null, // collectedClientData
                                null, // clientExtensions
                                null // transports
                );

                AuthenticationParameters authenticationParameters = new AuthenticationParameters(
                                new ServerProperty(new Origin(origin), rpId, null, null),
                                credentialRecord,
                                null, // userVerificationRequirement
                                false // userPresenceRequired
                );

                AuthenticationData authenticationData = webAuthnManager.parse(authenticationRequest);
                webAuthnManager.verify(authenticationData, authenticationParameters);

                credential.setSignCount(authenticationData.getAuthenticatorData().getSignCount());
                credentialRepository.save(credential);
        }

}