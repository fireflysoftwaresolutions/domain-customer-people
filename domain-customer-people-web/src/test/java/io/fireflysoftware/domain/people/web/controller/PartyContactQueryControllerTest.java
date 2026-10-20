package io.fireflysoftware.domain.people.web.controller;

import io.fireflysoftware.core.customer.sdk.model.PaginationResponse;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponseEmailContactDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponsePhoneContactDTO;
import org.fireflyframework.cqrs.query.QueryBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PartyContactQueryController Tests")
class PartyContactQueryControllerTest {

    @Mock
    private QueryBus queryBus;

    private PartyContactQueryController controller;

    @BeforeEach
    void setUp() {
        controller = new PartyContactQueryController(queryBus);
    }

    @Test
    @DisplayName("Should get party addresses successfully")
    void testGetPartyAddresses_ShouldReturnOkResponse() {
        // Given
        UUID partyId = UUID.randomUUID();
        PaginationResponse response = new PaginationResponse();
        when(queryBus.query(any())).thenReturn(Mono.just(response));

        // When
        Mono<ResponseEntity<PaginationResponse>> result = controller.getPartyAddresses(partyId);

        // Then
        StepVerifier.create(result)
                .assertNext(entity -> {
                    assertEquals(200, entity.getStatusCode().value());
                    assertNotNull(entity.getBody());
                })
                .verifyComplete();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should handle get party addresses errors")
    void testGetPartyAddresses_ShouldHandleErrors() {
        // Given
        UUID partyId = UUID.randomUUID();
        when(queryBus.query(any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When
        Mono<ResponseEntity<PaginationResponse>> result = controller.getPartyAddresses(partyId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should get party emails successfully")
    void testGetPartyEmails_ShouldReturnOkResponse() {
        // Given
        UUID partyId = UUID.randomUUID();
        PaginationResponseEmailContactDTO response = new PaginationResponseEmailContactDTO();
        when(queryBus.query(any())).thenReturn(Mono.just(response));

        // When
        Mono<ResponseEntity<PaginationResponseEmailContactDTO>> result = controller.getPartyEmails(partyId);

        // Then
        StepVerifier.create(result)
                .assertNext(entity -> {
                    assertEquals(200, entity.getStatusCode().value());
                    assertNotNull(entity.getBody());
                })
                .verifyComplete();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should handle get party emails errors")
    void testGetPartyEmails_ShouldHandleErrors() {
        // Given
        UUID partyId = UUID.randomUUID();
        when(queryBus.query(any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When
        Mono<ResponseEntity<PaginationResponseEmailContactDTO>> result = controller.getPartyEmails(partyId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should get party phones successfully")
    void testGetPartyPhones_ShouldReturnOkResponse() {
        // Given
        UUID partyId = UUID.randomUUID();
        PaginationResponsePhoneContactDTO response = new PaginationResponsePhoneContactDTO();
        when(queryBus.query(any())).thenReturn(Mono.just(response));

        // When
        Mono<ResponseEntity<PaginationResponsePhoneContactDTO>> result = controller.getPartyPhones(partyId);

        // Then
        StepVerifier.create(result)
                .assertNext(entity -> {
                    assertEquals(200, entity.getStatusCode().value());
                    assertNotNull(entity.getBody());
                })
                .verifyComplete();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should handle get party phones errors")
    void testGetPartyPhones_ShouldHandleErrors() {
        // Given
        UUID partyId = UUID.randomUUID();
        when(queryBus.query(any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When
        Mono<ResponseEntity<PaginationResponsePhoneContactDTO>> result = controller.getPartyPhones(partyId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should get party identity documents successfully")
    void testGetPartyIdentityDocuments_ShouldReturnOkResponse() {
        // Given
        UUID partyId = UUID.randomUUID();
        PaginationResponse response = new PaginationResponse();
        when(queryBus.query(any())).thenReturn(Mono.just(response));

        // When
        Mono<ResponseEntity<PaginationResponse>> result = controller.getPartyIdentityDocuments(partyId);

        // Then
        StepVerifier.create(result)
                .assertNext(entity -> {
                    assertEquals(200, entity.getStatusCode().value());
                    assertNotNull(entity.getBody());
                })
                .verifyComplete();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should handle get party identity documents errors")
    void testGetPartyIdentityDocuments_ShouldHandleErrors() {
        // Given
        UUID partyId = UUID.randomUUID();
        when(queryBus.query(any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When
        Mono<ResponseEntity<PaginationResponse>> result = controller.getPartyIdentityDocuments(partyId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should get party consents successfully")
    void testGetPartyConsents_ShouldReturnOkResponse() {
        // Given
        UUID partyId = UUID.randomUUID();
        PaginationResponse response = new PaginationResponse();
        when(queryBus.query(any())).thenReturn(Mono.just(response));

        // When
        Mono<ResponseEntity<PaginationResponse>> result = controller.getPartyConsents(partyId);

        // Then
        StepVerifier.create(result)
                .assertNext(entity -> {
                    assertEquals(200, entity.getStatusCode().value());
                    assertNotNull(entity.getBody());
                })
                .verifyComplete();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Should handle get party consents errors")
    void testGetPartyConsents_ShouldHandleErrors() {
        // Given
        UUID partyId = UUID.randomUUID();
        when(queryBus.query(any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When
        Mono<ResponseEntity<PaginationResponse>> result = controller.getPartyConsents(partyId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(queryBus).query(any());
    }

    @Test
    @DisplayName("Constructor should set QueryBus dependency")
    void testConstructor_ShouldSetQueryBus() {
        // When
        PartyContactQueryController newController = new PartyContactQueryController(queryBus);

        // Then
        assertNotNull(newController);
    }
}
