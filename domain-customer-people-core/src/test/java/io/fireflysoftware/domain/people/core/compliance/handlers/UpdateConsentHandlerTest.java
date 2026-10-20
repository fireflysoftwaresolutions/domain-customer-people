package io.fireflysoftware.domain.people.core.compliance.handlers;

import io.fireflysoftware.core.customer.sdk.api.ConsentsApi;
import io.fireflysoftware.core.customer.sdk.model.ConsentDTO;
import io.fireflysoftware.domain.people.core.compliance.commands.UpdateConsentCommand;
import org.fireflyframework.web.error.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateConsentHandler Tests")
class UpdateConsentHandlerTest {

    @Mock
    private ConsentsApi consentsApi;

    private UpdateConsentHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UpdateConsentHandler(consentsApi);
    }

    /**
     * Defect F: the PUT body must carry the consentTypeId (and other identity fields)
     * from the existing consent record — otherwise core rejects with HTTP 400
     * "Consent type ID is required". This test proves the handler fetches first,
     * preserves consentTypeId, and only then writes back.
     */
    @Test
    @DisplayName("Should preserve consentTypeId from the existing consent on update")
    void update_preservesConsentTypeIdFromExistingDto() {
        UUID partyId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();
        UUID consentTypeId = UUID.randomUUID();
        UUID applicationId = UUID.randomUUID();

        ConsentDTO existing = new ConsentDTO(consentId);
        existing.setPartyId(partyId);
        existing.setConsentTypeId(consentTypeId);
        existing.setChannel("WEB");
        existing.setGranted(false);
        // grantedAt deliberately null — this is a previously-revoked consent being re-granted

        when(consentsApi.getConsentById(eq(partyId), eq(consentId), any()))
                .thenReturn(Mono.just(existing));
        when(consentsApi.updateConsent(eq(partyId), eq(consentId), any(ConsentDTO.class), any(String.class)))
                .thenReturn(Mono.just(existing));

        UpdateConsentCommand cmd = new UpdateConsentCommand(partyId, consentId, true, applicationId);

        StepVerifier.create(handler.doHandle(cmd))
                .verifyComplete();

        ArgumentCaptor<ConsentDTO> captor = ArgumentCaptor.forClass(ConsentDTO.class);
        verify(consentsApi).updateConsent(eq(partyId), eq(consentId), captor.capture(), any(String.class));

        ConsentDTO sent = captor.getValue();
        assertThat(sent.getConsentTypeId())
                .as("consentTypeId from the existing record must be preserved on PUT")
                .isEqualTo(consentTypeId);
        assertThat(sent.getChannel()).isEqualTo("WEB");
        assertThat(sent.getGranted()).isTrue();
        assertThat(sent.getApplicationId()).isEqualTo(applicationId);
        // grantedAt must be stamped because this is a transition from revoked -> granted
        assertThat(sent.getGrantedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should stamp revokedAt when transitioning to granted=false")
    void update_stampsRevokedAt_whenGrantedFalse() {
        UUID partyId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();

        ConsentDTO existing = new ConsentDTO(consentId);
        existing.setPartyId(partyId);
        existing.setConsentTypeId(UUID.randomUUID());
        existing.setChannel("MOBILE");
        existing.setGranted(true);
        existing.setGrantedAt(LocalDateTime.of(2025, 1, 15, 10, 0));

        when(consentsApi.getConsentById(eq(partyId), eq(consentId), any()))
                .thenReturn(Mono.just(existing));
        when(consentsApi.updateConsent(eq(partyId), eq(consentId), any(ConsentDTO.class), any(String.class)))
                .thenReturn(Mono.just(existing));

        UpdateConsentCommand cmd = new UpdateConsentCommand(partyId, consentId, false, null);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        StepVerifier.create(handler.doHandle(cmd))
                .verifyComplete();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        ArgumentCaptor<ConsentDTO> captor = ArgumentCaptor.forClass(ConsentDTO.class);
        verify(consentsApi).updateConsent(eq(partyId), eq(consentId), captor.capture(), any(String.class));

        ConsentDTO sent = captor.getValue();
        assertThat(sent.getGranted()).isFalse();
        assertThat(sent.getRevokedAt()).isNotNull();
        assertThat(sent.getRevokedAt()).isBetween(before, after);
        // The original grantedAt must remain untouched for audit
        assertThat(sent.getGrantedAt()).isEqualTo(LocalDateTime.of(2025, 1, 15, 10, 0));
    }

    @Test
    @DisplayName("Should preserve original grantedAt when re-affirming an already granted consent")
    void update_doesNotOverwriteGrantedAt_whenAlreadyGranted() {
        UUID partyId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();
        LocalDateTime originalGrantedAt = LocalDateTime.of(2025, 6, 1, 9, 30);

        ConsentDTO existing = new ConsentDTO(consentId);
        existing.setPartyId(partyId);
        existing.setConsentTypeId(UUID.randomUUID());
        existing.setGranted(true);
        existing.setGrantedAt(originalGrantedAt);

        when(consentsApi.getConsentById(eq(partyId), eq(consentId), any()))
                .thenReturn(Mono.just(existing));
        when(consentsApi.updateConsent(eq(partyId), eq(consentId), any(ConsentDTO.class), any(String.class)))
                .thenReturn(Mono.just(existing));

        UpdateConsentCommand cmd = new UpdateConsentCommand(partyId, consentId, true, null);

        StepVerifier.create(handler.doHandle(cmd))
                .verifyComplete();

        ArgumentCaptor<ConsentDTO> captor = ArgumentCaptor.forClass(ConsentDTO.class);
        verify(consentsApi).updateConsent(eq(partyId), eq(consentId), captor.capture(), any(String.class));

        ConsentDTO sent = captor.getValue();
        assertThat(sent.getGrantedAt())
                .as("re-affirming a grant must NOT overwrite the original grantedAt")
                .isEqualTo(originalGrantedAt);
    }

    @Test
    @DisplayName("Should map a 404 from getConsentById to BusinessException CONSENT_NOT_FOUND")
    void update_mapsNotFoundFromGetConsentById_toBusinessException() {
        UUID partyId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();

        WebClientResponseException notFound = WebClientResponseException.create(
                HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null);

        when(consentsApi.getConsentById(eq(partyId), eq(consentId), any()))
                .thenReturn(Mono.error(notFound));

        UpdateConsentCommand cmd = new UpdateConsentCommand(partyId, consentId, true, null);

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(BusinessException.class);
                    BusinessException be = (BusinessException) error;
                    assertThat(be.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(be.getCode()).isEqualTo("CONSENT_NOT_FOUND");
                })
                .verify();

        verify(consentsApi, never())
                .updateConsent(any(UUID.class), any(UUID.class), any(ConsentDTO.class), any(String.class));
    }

    @Test
    @DisplayName("Should use a deterministic idempotency key derived from partyId and consentId")
    void update_usesDeterministicIdempotencyKey() {
        UUID partyId = UUID.randomUUID();
        UUID consentId = UUID.randomUUID();

        ConsentDTO existing = new ConsentDTO(consentId);
        existing.setPartyId(partyId);
        existing.setConsentTypeId(UUID.randomUUID());
        existing.setGranted(true);

        when(consentsApi.getConsentById(eq(partyId), eq(consentId), any()))
                .thenReturn(Mono.just(existing));
        when(consentsApi.updateConsent(eq(partyId), eq(consentId), any(ConsentDTO.class), any(String.class)))
                .thenReturn(Mono.just(existing));

        UpdateConsentCommand cmd = new UpdateConsentCommand(partyId, consentId, true, null);

        // Run the same operation twice — the captured idempotency key must be identical.
        StepVerifier.create(handler.doHandle(cmd)).verifyComplete();
        StepVerifier.create(handler.doHandle(cmd)).verifyComplete();

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(consentsApi, org.mockito.Mockito.times(2))
                .updateConsent(eq(partyId), eq(consentId), any(ConsentDTO.class), keyCaptor.capture());

        assertThat(keyCaptor.getAllValues())
                .as("retries of the same logical operation must collapse to the same idempotency key")
                .hasSize(2)
                .allMatch(k -> k.equals(keyCaptor.getAllValues().get(0)));
    }
}
