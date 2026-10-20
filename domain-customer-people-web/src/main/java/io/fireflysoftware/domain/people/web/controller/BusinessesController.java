package io.fireflysoftware.domain.people.web.controller;

import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.core.customer.sdk.model.PartyStatusDTO;
import io.fireflysoftware.domain.people.core.business.commands.RegisterBusinessCommand;
import io.fireflysoftware.domain.people.core.business.commands.UpdateBusinessCommand;
import io.fireflysoftware.domain.people.core.business.services.BusinessService;
import io.fireflysoftware.domain.people.core.contact.commands.*;
import io.fireflysoftware.domain.people.core.contact.services.ContactService;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterCustomerCommand;
import io.fireflysoftware.domain.people.core.status.commands.UpdateStatusCommand;
import io.fireflysoftware.domain.people.core.status.services.StatusService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/businesses")
@RequiredArgsConstructor
@Tag(name = "Businesses", description = "CQ queries and registration for businesses")
public class BusinessesController {

    private final BusinessService businessService;
    private final ContactService contactService;
    private final StatusService statusService;


    @PostMapping
    @Operation(summary = "Register a business", description = "Registers a business and returns the resulting identifiers")
    public Mono<ResponseEntity<Object>> registerBusiness(@Valid @RequestBody RegisterBusinessCommand command) {
        return businessService.registerBusiness(command)
                .map(saga -> {
                    Map<String, Object> body = new HashMap<>();
                    saga.resultOf("registerParty", UUID.class)
                            .filter(id -> !ZERO_UUID.equals(id))
                            .ifPresent(id -> body.put("partyId", id.toString()));
                    saga.resultOf("registerLegalEntity", UUID.class)
                            .filter(id -> !ZERO_UUID.equals(id))
                            .ifPresent(id -> body.put("legalEntityId", id.toString()));
                    return ResponseEntity.ok((Object) body);
                });
    }

    private static final UUID ZERO_UUID = new UUID(0L, 0L);

    @PutMapping
    @Operation(summary = "Update business", description = "Updates the name of an existing business")
    public Mono<ResponseEntity<Object>> updateBusiness(
            @Valid @RequestBody UpdateBusinessCommand command) {
        return businessService.updateBusiness(command)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(summary = "Get business info", description = "Retrieve business information by business ID.")
    @GetMapping("/{businessId}")
    public Mono<ResponseEntity<LegalEntityDTO>> getBusinessInfo(@PathVariable UUID businessId) {
        return businessService.getBusinessInfo(businessId)
                .map(ResponseEntity::ok);
    }

    // Address endpoints
    @PostMapping("/{partyId}/addresses")
    @Operation(summary = "Add business address", description = "Add an address with validity period; prevent primary overlaps.")
    public Mono<ResponseEntity<Object>> addBusinessAddress(
            @PathVariable("partyId") UUID partyId,
            @RequestBody RegisterAddressCommand addressCommand) {
        return contactService.addAddress(partyId, addressCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PutMapping("/{partyId}/addresses/{addressId}")
    @Operation(summary = "Update business address", description = "Modify address fields keeping version history.")
    public Mono<ResponseEntity<Object>> updateBusinessAddress(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("addressId") UUID addressId,
            @RequestBody UpdateAddressCommand addressData) {
        return contactService.updateAddress(partyId, addressId, addressData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/addresses/{addressId}")
    @Operation(summary = "Remove business address", description = "Remove or end-date an address; preserve audit.")
    public Mono<ResponseEntity<Object>> removeBusinessAddress(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("addressId") UUID addressId) {
        return contactService.removeAddress(partyId, addressId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Email endpoints
    @PostMapping("/{partyId}/emails")
    @Operation(summary = "Add business email", description = "Add email; validate format and uniqueness within business.")
    public Mono<ResponseEntity<Object>> addBusinessEmail(
            @PathVariable("partyId") UUID partyId,
            @RequestBody @Valid RegisterEmailCommand emailCommand) {
        return contactService.addEmail(partyId, emailCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PutMapping("/{partyId}/emails/{emailId}")
    @Operation(summary = "Update business email", description = "Modify email fields keeping version history.")
    public Mono<ResponseEntity<Object>> updateBusinessEmail(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("emailId") UUID emailId,
            @RequestBody UpdateEmailCommand emailData) {
        return contactService.updateEmail(partyId, emailId, emailData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/emails/{emailId}")
    @Operation(summary = "Remove business email", description = "Remove email from profile.")
    public Mono<ResponseEntity<Object>> removeBusinessEmail(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("emailId") UUID emailId) {
        return contactService.removeEmail(partyId, emailId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Phone endpoints
    @PostMapping("/{partyId}/phones")
    @Operation(summary = "Add business phone", description = "Add phone in E.164 with label (mobile/landline).")
    public Mono<ResponseEntity<Object>> addBusinessPhone(
            @PathVariable("partyId") UUID partyId,
            @RequestBody @Valid RegisterPhoneCommand phoneCommand) {
        return contactService.addPhone(partyId, phoneCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PutMapping("/{partyId}/phones/{phoneId}")
    @Operation(summary = "Update business email", description = "Modify phone fields keeping version history.")
    public Mono<ResponseEntity<Object>> updateBusinessPhone(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("phoneId") UUID phoneId,
            @RequestBody UpdatePhoneCommand phoneData) {
        return contactService.updatePhone(partyId, phoneId, phoneData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/phones/{phoneId}")
    @Operation(summary = "Remove business phone", description = "Remove phone from profile.")
    public Mono<ResponseEntity<Object>> removeBusinessPhone(
            @PathVariable("partyId") UUID partyId,
            @PathVariable("phoneId") UUID phoneId) {
        return contactService.removePhone(partyId, phoneId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Status management endpoints
    @PostMapping("/{partyId}/dormant")
    @Operation(summary = "Mark business dormant", description = "Flag profile as dormant due to inactivity.")
    public Mono<ResponseEntity<Object>> markBusinessDormant(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.INACTIVE)
                        .withReason("User has been marked as dormant due to inactivity"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/reactivate")
    @Operation(summary = "Reactivate business", description = "Reactivate a dormant profile.")
    public Mono<ResponseEntity<Object>> reactivateBusiness(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.ACTIVE)
                        .withReason("User account has been reactivated and is now fully usable"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/defunct")
    @Operation(summary = "Mark business defunct", description = "Mark as defunct and block dependent operations.")
    public Mono<ResponseEntity<Object>> markBusinessDefunct(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.CLOSED)
                        .withReason("User account is permanently closed because the user is defunct"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/closure-request")
    @Operation(summary = "Request business closure", description = "Request customer closure once obligations are zero.")
    public Mono<ResponseEntity<Object>> requestBusinessClosure(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.PENDING)
                        .withReason("A closure request has been submitted but is not yet confirmed"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/confirm-closure")
    @Operation(summary = "Confirm business closure", description = "Confirm closure after checks pass.")
    public Mono<ResponseEntity<Object>> confirmBusinessClosure(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.CLOSED)
                        .withReason("Closure has been confirmed and the account is permanently closed"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/lock")
    @Operation(summary = "Lock business profile", description = "Lock profile for audit/investigation; block writes.")
    public Mono<ResponseEntity<Object>> lockBusinessProfile(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.SUSPENDED)
                        .withReason("Profile is temporarily locked, restricting access and activity"))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/unlock")
    @Operation(summary = "Unlock business profile", description = "Unlock profile for changes.")
    public Mono<ResponseEntity<Object>> unlockBusinessProfile(@PathVariable("partyId") UUID partyId) {
        return statusService.updateStatus(new UpdateStatusCommand()
                        .withPartyId(partyId)
                        .withStatusCode(PartyStatusDTO.StatusCodeEnum.ACTIVE)
                        .withReason("Lock has been removed; user profile is restored to active status"))
                .thenReturn(ResponseEntity.ok().build());
    }
}
