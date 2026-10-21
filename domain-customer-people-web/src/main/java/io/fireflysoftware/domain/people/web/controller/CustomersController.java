/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.people.web.controller;

import io.fireflysoftware.core.customer.sdk.model.FilterRequestPartyDTO;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import io.fireflysoftware.core.customer.sdk.model.PartyStatusDTO;
import io.fireflysoftware.domain.people.core.customer.dto.CustomerSearchResultDTO;
import io.fireflysoftware.domain.people.core.compliance.commands.UpdateConsentCommand;
import io.fireflysoftware.domain.people.core.compliance.services.ConsentService;
import io.fireflysoftware.domain.people.core.contact.commands.*;
import io.fireflysoftware.domain.people.core.contact.services.ContactService;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterCustomerCommand;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
import io.fireflysoftware.domain.people.core.customer.dto.CustomerFinancialProfileDTO;
import io.fireflysoftware.domain.people.core.customer.services.CustomerService;
import io.fireflysoftware.domain.people.core.status.commands.UpdateStatusCommand;
import io.fireflysoftware.domain.people.core.status.services.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "CQ queries and registration for customers")
public class CustomersController {

    private final CustomerService customerService;
    private final ContactService contactService;
    private final StatusService statusService;
    private final ConsentService consentService;


    @PostMapping
    @Operation(summary = "Register a customer", description = "Registers a customer and returns the resulting identifiers")
    public Mono<ResponseEntity<Object>> registerCustomer(@Valid @RequestBody RegisterCustomerCommand command) {
        return customerService.registerCustomer(command)
                .map(saga -> {
                    Map<String, Object> body = new HashMap<>();
                    saga.resultOf("registerParty", UUID.class)
                            .filter(id -> !ZERO_UUID.equals(id))
                            .ifPresent(id -> body.put("partyId", id.toString()));
                    saga.resultOf("registerNaturalPerson", UUID.class)
                            .filter(id -> !ZERO_UUID.equals(id))
                            .ifPresent(id -> body.put("naturalPersonId", id.toString()));
                    saga.resultOf("registerLegalEntity", UUID.class)
                            .filter(id -> !ZERO_UUID.equals(id))
                            .ifPresent(id -> body.put("legalEntityId", id.toString()));
                    return ResponseEntity.ok((Object) body);
                });
    }

    private static final UUID ZERO_UUID = new UUID(0L, 0L);

    @PutMapping
    @Operation(summary = "Update customer", description = "Updates an existing customer")
    public Mono<ResponseEntity<Object>> updateCustomer(
            @Valid @RequestBody UpdateCustomerCommand command) {
        return customerService.updateCustomer(command)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Get customer info", description = "Retrieve customer information by customer ID.")
    @GetMapping("/{customerId}")
    public Mono<ResponseEntity<NaturalPersonDTO>> getCustomerInfo(@PathVariable UUID customerId) {
        return customerService.getCustomerInfo(customerId)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Get customer financial profile",
            description = "Aggregates the party's contracts, loan applications, servicing cases, "
                    + "personal-loan agreements and supply-chain finance agreements across the "
                    + "core lending and contract services. Empty sections are returned as empty "
                    + "lists when the party has no rows in the underlying table."
    )
    @GetMapping("/{partyId}/financial-profile")
    public Mono<ResponseEntity<CustomerFinancialProfileDTO>> getCustomerFinancialProfile(
            @PathVariable("partyId") UUID partyId) {
        return customerService.getFinancialProfile(partyId)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Search customers",
            description = "Proxies POST /api/v1/parties/filter on core-common-customer-mgmt "
                    + "and enriches each matching party with its associated natural-person "
                    + "(INDIVIDUAL) or legal-entity (ORGANIZATION) record. The response is a "
                    + "paginated list of EnrichedPartyDTO with the bare party plus exactly one "
                    + "of the two association fields populated. A failed enrichment lookup is "
                    + "logged and returns the row with only the bare party."
    )
    @PostMapping("/search")
    public Mono<ResponseEntity<CustomerSearchResultDTO>> searchCustomers(
            @RequestBody FilterRequestPartyDTO filterRequest) {
        return customerService.filterCustomers(filterRequest)
                .map(ResponseEntity::ok);
    }

    // Address endpoints
    @PostMapping("/{partyId}/addresses")
    @Operation(summary = "Add customer address", description = "Add an address with validity period; prevent primary overlaps.")
    public Mono<ResponseEntity<Object>> addCustomerAddress(
            @PathVariable("partyId") UUID partyId,
            @RequestBody RegisterAddressCommand addressCommand) {
        return contactService.addAddress(partyId, addressCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PutMapping("/{partyId}/addresses/{addressId}")
    @Operation(summary = "Update customer address", description = "Modify address fields keeping version history.")
    public Mono<ResponseEntity<Object>> updateCustomerAddress(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("addressId") UUID addressId,
            @RequestBody UpdateAddressCommand addressData) {
        return contactService.updateAddress(partyId, addressId, addressData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/addresses/{addressId}")
    @Operation(summary = "Remove customer address", description = "Remove or end-date an address; preserve audit.")
    public Mono<ResponseEntity<Object>> removeCustomerAddress(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("addressId") UUID addressId) {
        return contactService.removeAddress(partyId, addressId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Email endpoints
    @PostMapping("/{partyId}/emails")
    @Operation(summary = "Add customer email", description = "Add email; validate format and uniqueness within customer.")
    public Mono<ResponseEntity<Object>> addCustomerEmail(
            @PathVariable("partyId") UUID partyId,
            @RequestBody @Valid RegisterEmailCommand emailCommand) {
        return contactService.addEmail(partyId, emailCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PutMapping("/{partyId}/emails/{emailId}")
    @Operation(summary = "Update customer email", description = "Modify email fields keeping version history.")
    public Mono<ResponseEntity<Object>> updateCustomerEmail(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("emailId") UUID emailId,
            @RequestBody UpdateEmailCommand emailData) {
        return contactService.updateEmail(partyId, emailId, emailData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/emails/{emailId}")
    @Operation(summary = "Remove customer email", description = "Remove email from profile.")
    public Mono<ResponseEntity<Object>> removeCustomerEmail(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("emailId") UUID emailId) {
        return contactService.removeEmail(partyId, emailId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Phone endpoints
    @PostMapping("/{partyId}/phones")
    @Operation(summary = "Add customer phone", description = "Add phone in E.164 with label (mobile/landline).")
    public Mono<ResponseEntity<Object>> addCustomerPhone(
            @PathVariable("partyId") UUID partyId,
            @RequestBody @Valid RegisterPhoneCommand phoneCommand) {
        return contactService.addPhone(partyId, phoneCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PutMapping("/{partyId}/phones/{phoneId}")
    @Operation(summary = "Update customer email", description = "Modify phone fields keeping version history.")
    public Mono<ResponseEntity<Object>> updateCustomerPhone(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("phoneId") UUID phoneId,
            @RequestBody UpdatePhoneCommand phoneData) {
        return contactService.updatePhone(partyId, phoneId, phoneData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/phones/{phoneId}")
    @Operation(summary = "Remove customer phone", description = "Remove phone from profile.")
    public Mono<ResponseEntity<Object>> removeCustomerPhone(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("phoneId") UUID phoneId) {
        return contactService.removePhone(partyId, phoneId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Tax id endpoints
    @PostMapping("/{partyId}/id-documents")
    @Operation(summary = "Add tax id", description = "Add secondary tax ID; validate format/uniqueness")
    public Mono<ResponseEntity<Object>> addTaxId(
            @PathVariable("partyId") UUID partyId,
            @Valid @RequestBody RegisterIdentityDocumentCommand command) {
        return contactService.addIdentityDocument(partyId, command)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{partyId}/id-documents/{taxId}")
    @Operation(summary = "Remove tax id", description = "Remove a tax ID from the profile")
    public Mono<ResponseEntity<Object>> removeTaxId(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("taxId") UUID taxId) {
        return contactService.removeIdentityDocument(partyId, taxId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Consent endpoints
    @PutMapping("/{partyId}/consents/{consentId}")
    @Operation(
            summary = "Update customer consent",
            description = "Upserts the consent record identified by (partyId, consentId). "
                    + "Sets the granted flag and an optional applicationId soft link to the "
                    + "originating loan or onboarding application.",
            operationId = "updateCustomerConsent"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consent updated"),
            @ApiResponse(responseCode = "400", description = "Invalid consent payload"),
            @ApiResponse(responseCode = "404", description = "Party or consent record not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<Object>> updateCustomerConsent(
            @Parameter(description = "The owning party identifier", required = true)
            @PathVariable("partyId") UUID partyId,
            @Parameter(description = "The consent record identifier", required = true)
            @PathVariable("consentId") UUID consentId,
            @Valid @RequestBody UpdateConsentCommand command) {
        return consentService.updateConsent(partyId, consentId, command)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Preferred channel endpoint
    @PostMapping("/{partyId}/preferred-channel")
    @Operation(summary = "Set preferred channel", description = "Set preferred contact channel (email/phone); one per type.")
    public Mono<ResponseEntity<Object>> setPreferredChannel(
            @PathVariable("partyId") UUID partyId,
            @RequestBody UpdatePreferredChannelCommand channelData) {
        return contactService.setPreferredChannel(partyId, channelData)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Status management endpoints
    @PostMapping("/{partyId}/dormant")
    @Operation(summary = "Mark customer dormant", description = "Flag profile as dormant due to inactivity.")
    public Mono<ResponseEntity<Object>> markCustomerDormant(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.INACTIVE)
                        .withReason("User has been marked as dormant due to inactivity"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/reactivate")
    @Operation(summary = "Reactivate customer", description = "Reactivate a dormant profile.")
    public Mono<ResponseEntity<Object>> reactivateCustomer(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.ACTIVE)
                        .withReason("User account has been reactivated and is now fully usable"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/deceased")
    @Operation(summary = "Mark customer deceased", description = "Mark as deceased and block dependent operations.")
    public Mono<ResponseEntity<Object>> markCustomerDeceased(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.CLOSED)
                        .withReason("User account is permanently closed because the user is deceased"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/closure-request")
    @Operation(summary = "Request customer closure", description = "Request customer closure once obligations are zero.")
    public Mono<ResponseEntity<Object>> requestCustomerClosure(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.PENDING)
                        .withReason("A closure request has been submitted but is not yet confirmed"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/confirm-closure")
    @Operation(summary = "Confirm customer closure", description = "Confirm closure after checks pass.")
    public Mono<ResponseEntity<Object>> confirmCustomerClosure(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.CLOSED)
                        .withReason("Closure has been confirmed and the account is permanently closed"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/lock")
    @Operation(summary = "Lock customer profile", description = "Lock profile for audit/investigation; block writes.")
    public Mono<ResponseEntity<Object>> lockCustomerProfile(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.SUSPENDED)
                        .withReason("Profile is temporarily locked, restricting access and activity"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/unlock")
    @Operation(summary = "Unlock customer profile", description = "Unlock profile for changes.")
    public Mono<ResponseEntity<Object>> unlockCustomerProfile(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.ACTIVE)
                        .withReason("Lock has been removed; user profile is restored to active status"))
                .thenReturn(ResponseEntity.ok().build());
    }

}
