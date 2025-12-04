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
import com.webauthn4j.data.PublicKeyCredentialDescriptor;
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
import com.webauthn4j.data.AuthenticatorSelectionCriteria;
import com.webauthn4j.data.AuthenticatorAttachment;
import com.webauthn4j.data.AttestationConveyancePreference;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebAuthnService {

        private final WebAuthnCredentialRepository credentialRepository;
        private final WebAuthnManager webAuthnManager;
        private final com.poly.livre.backend.repositories.UserRepository userRepository;

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

                String challengeString = java.util.Base64.getUrlEncoder().withoutPadding()
                                .encodeToString(challenge.getValue());
                user.setCurrentChallenge(challengeString);
                userRepository.save(user);

                List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(
                                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY,
                                                COSEAlgorithmIdentifier.ES256),
                                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY,
                                                COSEAlgorithmIdentifier.RS256));

                List<PublicKeyCredentialDescriptor> excludeCredentials = credentialRepository
                                .findAllByUserId(user.getId())
                                .stream()
                                .map(credential -> new PublicKeyCredentialDescriptor(
                                                PublicKeyCredentialType.PUBLIC_KEY,
                                                java.util.Base64.getUrlDecoder().decode(credential.getCredentialId()),
                                                null)) // transports
                                .toList();

                AuthenticatorSelectionCriteria authenticatorSelection = new AuthenticatorSelectionCriteria(
                                AuthenticatorAttachment.PLATFORM,
                                true, // requireResidentKey
                                UserVerificationRequirement.PREFERRED);

                return new PublicKeyCredentialCreationOptions(
                                rpEntity,
                                userEntity,
                                challenge,
                                pubKeyCredParams,
                                60000L, // timeout
                                excludeCredentials,
                                authenticatorSelection,
                                AttestationConveyancePreference.NONE,
                                null // extensions
                );
        }

        public PublicKeyCredentialRequestOptions startAuthentication(User user) {
                Challenge challenge = new DefaultChallenge();
                String challengeString = java.util.Base64.getUrlEncoder().withoutPadding()
                                .encodeToString(challenge.getValue());
                user.setCurrentChallenge(challengeString);
                userRepository.save(user);

                return new PublicKeyCredentialRequestOptions(
                                challenge,
                                60000L,
                                rpId,
                                null,
                                UserVerificationRequirement.PREFERRED,
                                null);
        }

        public Map<String, Object> startDiscoverableLogin() {
                Challenge challenge = new DefaultChallenge();
                String challengeString = java.util.Base64.getUrlEncoder().withoutPadding()
                                .encodeToString(challenge.getValue());

                PublicKeyCredentialRequestOptions options = new PublicKeyCredentialRequestOptions(
                                challenge,
                                60000L,
                                rpId,
                                Collections.emptyList(), // Allow any credential (discoverable)
                                UserVerificationRequirement.PREFERRED,
                                null);

                return Map.of("options", options, "challenge", challengeString);
        }

        @Transactional
        public User finishDiscoverableLogin(String challengeString, AuthenticationFinishRequest request) {
                AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                                java.util.Base64.getUrlDecoder().decode(request.getId()),
                                java.util.Base64.getUrlDecoder().decode(request.getResponse().getAuthenticatorData()),
                                java.util.Base64.getUrlDecoder().decode(request.getResponse().getClientDataJSON()),
                                java.util.Base64.getUrlDecoder().decode(request.getResponse().getSignature()));

                Challenge challenge = new DefaultChallenge(java.util.Base64.getUrlDecoder().decode(challengeString));

                AuthenticationData authenticationData = webAuthnManager.parse(authenticationRequest);

                WebAuthnCredential credential = credentialRepository.findByCredentialId(request.getId())
                                .orElseThrow(() -> new RuntimeException("Credential not found"));

                User user = credential.getUser();

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
                                new ServerProperty(new Origin(origin), rpId, challenge, null),
                                credentialRecord,
                                null, // userVerificationRequirement
                                false // userPresenceRequired
                );

                webAuthnManager.verify(authenticationData, authenticationParameters);

                credential.setSignCount(authenticationData.getAuthenticatorData().getSignCount());
                credentialRepository.save(credential);

                return user;
        }

        @Transactional
        public void finishRegistration(User user, String name, RegistrationFinishRequest request) {
                RegistrationRequest registrationRequest = new RegistrationRequest(
                                java.util.Base64.getUrlDecoder().decode(request.getResponse().getAttestationObject()),
                                java.util.Base64.getUrlDecoder().decode(request.getResponse().getClientDataJSON()));

                String savedChallenge = user.getCurrentChallenge();
                if (savedChallenge == null)
                        throw new RuntimeException("No challenge found for user");

                Challenge challenge = new DefaultChallenge(java.util.Base64.getUrlDecoder().decode(savedChallenge));

                RegistrationParameters registrationParameters = new RegistrationParameters(
                                new ServerProperty(new Origin(origin), rpId, challenge, null),
                                null, // UserVerificationRequirement
                                false // userPresenceRequired
                );

                user.setCurrentChallenge(null);
                userRepository.save(user);

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
                                .name(name)
                                .build();

                credentialRepository.save(credential);
        }

        @Transactional
        public void finishLogin(User user, AuthenticationFinishRequest request) {
                WebAuthnCredential credential = credentialRepository.findByCredentialId(request.getId())
                                .orElseThrow(() -> new RuntimeException("Credential not found"));

                AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                                java.util.Base64.getUrlDecoder().decode(request.getId()),
                                java.util.Base64.getUrlDecoder().decode(request.getResponse().getAuthenticatorData()),
                                java.util.Base64.getUrlDecoder().decode(request.getResponse().getClientDataJSON()),
                                java.util.Base64.getUrlDecoder().decode(request.getResponse().getSignature()));

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

                String savedChallenge = user.getCurrentChallenge();
                if (savedChallenge == null)
                        throw new RuntimeException("No challenge found for user");
                Challenge challenge = new DefaultChallenge(java.util.Base64.getUrlDecoder().decode(savedChallenge));

                AuthenticationParameters authenticationParameters = new AuthenticationParameters(
                                new ServerProperty(new Origin(origin), rpId, challenge, null),
                                credentialRecord,
                                null, // userVerificationRequirement
                                false // userPresenceRequired
                );

                user.setCurrentChallenge(null);
                userRepository.save(user);

                AuthenticationData authenticationData = webAuthnManager.parse(authenticationRequest);
                webAuthnManager.verify(authenticationData, authenticationParameters);

                credential.setSignCount(authenticationData.getAuthenticatorData().getSignCount());
                credentialRepository.save(credential);
        }

}