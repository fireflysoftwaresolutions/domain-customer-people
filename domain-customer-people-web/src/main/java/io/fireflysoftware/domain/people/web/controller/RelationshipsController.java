package io.fireflysoftware.domain.people.web.controller;

import io.fireflysoftware.domain.people.core.customer.commands.RegisterCustomerCommand;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.UpdatePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.services.PartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/party-relationships")
@RequiredArgsConstructor
@Tag(name = "Relationships", description = "CQ queries and registration for party relationships")
public class RelationshipsController {

    private final PartyService partyService;

    @PostMapping
    @Operation(summary = "Register a relationship", description = "Registers a relationship")
    public Mono<ResponseEntity<Object>> registerRelationship(@Valid @RequestBody RegisterPartyRelationshipCommand command) {
        return partyService.registerPartyRelationship(command)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PutMapping("/{relationId}")
    @Operation(summary = "Update relationship", description = "Updates an existing relationship")
    public Mono<ResponseEntity<Object>> updateRelationship(
            @PathVariable("relationId") UUID relationId,
            @Valid @RequestBody UpdatePartyRelationshipCommand command) {
        return partyService.updatePartyRelationship(command, relationId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{relationId}")
    @Operation(summary = "Remove relationship", description = "Remove relationship.")
    public Mono<ResponseEntity<Object>> removeRelationship(
            @PathVariable("relationId") UUID relationId) {
        return partyService.removePartyRelationship(relationId)
                .thenReturn(ResponseEntity.ok().build());
    }

}
