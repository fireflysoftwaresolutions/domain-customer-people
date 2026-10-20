package io.fireflysoftware.domain.people.web.controller;

import io.fireflysoftware.core.customer.sdk.model.PaginationResponse;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponseEmailContactDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponsePhoneContactDTO;
import io.fireflysoftware.domain.people.core.customer.queries.PartyAddressesQuery;
import io.fireflysoftware.domain.people.core.customer.queries.PartyConsentsQuery;
import io.fireflysoftware.domain.people.core.customer.queries.PartyEmailsQuery;
import io.fireflysoftware.domain.people.core.customer.queries.PartyIdentityDocumentsQuery;
import io.fireflysoftware.domain.people.core.customer.queries.PartyPhonesQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.query.QueryBus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/parties")
@RequiredArgsConstructor
@Tag(name = "Party Contact Queries", description = "Query endpoints for party contact information")
public class PartyContactQueryController {

    private final QueryBus queryBus;

    @GetMapping("/{partyId}/addresses")
    @Operation(summary = "Get party addresses", description = "Retrieve all addresses for a given party.")
    public Mono<ResponseEntity<PaginationResponse>> getPartyAddresses(
            @Parameter(description = "The party identifier") @PathVariable("partyId") UUID partyId) {
        log.debug("GET /api/v1/parties/{}/addresses", partyId);
        return queryBus.<PaginationResponse>query(PartyAddressesQuery.builder().partyId(partyId).build())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{partyId}/emails")
    @Operation(summary = "Get party emails", description = "Retrieve all email contacts for a given party.")
    public Mono<ResponseEntity<PaginationResponseEmailContactDTO>> getPartyEmails(
            @Parameter(description = "The party identifier") @PathVariable("partyId") UUID partyId) {
        log.debug("GET /api/v1/parties/{}/emails", partyId);
        return queryBus.<PaginationResponseEmailContactDTO>query(PartyEmailsQuery.builder().partyId(partyId).build())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{partyId}/phones")
    @Operation(summary = "Get party phones", description = "Retrieve all phone contacts for a given party.")
    public Mono<ResponseEntity<PaginationResponsePhoneContactDTO>> getPartyPhones(
            @Parameter(description = "The party identifier") @PathVariable("partyId") UUID partyId) {
        log.debug("GET /api/v1/parties/{}/phones", partyId);
        return queryBus.<PaginationResponsePhoneContactDTO>query(PartyPhonesQuery.builder().partyId(partyId).build())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{partyId}/identity-documents")
    @Operation(summary = "Get party identity documents", description = "Retrieve all identity documents for a given party.")
    public Mono<ResponseEntity<PaginationResponse>> getPartyIdentityDocuments(
            @Parameter(description = "The party identifier") @PathVariable("partyId") UUID partyId) {
        log.debug("GET /api/v1/parties/{}/identity-documents", partyId);
        return queryBus.<PaginationResponse>query(PartyIdentityDocumentsQuery.builder().partyId(partyId).build())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{partyId}/consents")
    @Operation(summary = "Get party consents", description = "Retrieve all consents for a given party.")
    public Mono<ResponseEntity<PaginationResponse>> getPartyConsents(
            @Parameter(description = "The party identifier") @PathVariable("partyId") UUID partyId) {
        log.debug("GET /api/v1/parties/{}/consents", partyId);
        return queryBus.<PaginationResponse>query(PartyConsentsQuery.builder().partyId(partyId).build())
                .map(ResponseEntity::ok);
    }
}
