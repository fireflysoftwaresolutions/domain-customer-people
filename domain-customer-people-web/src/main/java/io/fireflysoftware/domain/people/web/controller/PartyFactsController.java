package io.fireflysoftware.domain.people.web.controller;

import io.fireflysoftware.domain.people.core.attribute.commands.RegisterPartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.RemovePartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.UpdatePartyAttributeCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RegisterPartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RemovePartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.commands.UpdatePartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.services.PartyFactsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parties/{partyId}")
@RequiredArgsConstructor
@Tag(name = "Party facts", description = "Industry classification, party attributes, and ownership hierarchy")
public class PartyFactsController {

    private final PartyFactsService partyFactsService;

    @PostMapping("/industry-classifications")
    @Operation(summary = "Store an industry classification",
            description = "Records a classification scheme and code. Schemes are ISIC, NACE, NAICS, SIC, CNAE, and OTHER.")
    public Mono<ResponseEntity<Object>> registerClassification(
            @PathVariable UUID partyId,
            @Valid @RequestBody RegisterPartyIndustryClassificationCommand command) {
        return partyFactsService.registerClassification(command.withPartyId(partyId))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PutMapping("/industry-classifications/{classificationId}")
    @Operation(summary = "Replace an industry classification")
    public Mono<ResponseEntity<Object>> updateClassification(
            @PathVariable UUID partyId,
            @PathVariable UUID classificationId,
            @Valid @RequestBody UpdatePartyIndustryClassificationCommand command) {
        return partyFactsService.updateClassification(command.withPartyId(partyId).withClassificationId(classificationId))
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/industry-classifications/{classificationId}")
    @Operation(summary = "Delete an industry classification")
    public Mono<ResponseEntity<Object>> removeClassification(
            @PathVariable UUID partyId,
            @PathVariable UUID classificationId) {
        return partyFactsService.removeClassification(new RemovePartyIndustryClassificationCommand(partyId, classificationId))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/attributes")
    @Operation(summary = "Store a party attribute",
            description = "Employment, housing, loans, and debts are effective-dated attributes. Occupation and monthly income stay on the person.")
    public Mono<ResponseEntity<Object>> registerAttribute(
            @PathVariable UUID partyId,
            @Valid @RequestBody RegisterPartyAttributeCommand command) {
        return partyFactsService.registerAttribute(command.withPartyId(partyId))
                .thenReturn(ResponseEntity.ok().build());
    }

    @PutMapping("/attributes/{attributeId}")
    @Operation(summary = "Replace a party attribute")
    public Mono<ResponseEntity<Object>> updateAttribute(
            @PathVariable UUID partyId,
            @PathVariable UUID attributeId,
            @Valid @RequestBody UpdatePartyAttributeCommand command) {
        return partyFactsService.updateAttribute(command.withPartyId(partyId).withAttributeId(attributeId))
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/attributes/{attributeId}")
    @Operation(summary = "Delete a party attribute")
    public Mono<ResponseEntity<Object>> removeAttribute(
            @PathVariable UUID partyId,
            @PathVariable UUID attributeId) {
        return partyFactsService.removeAttribute(new RemovePartyAttributeCommand(partyId, attributeId))
                .thenReturn(ResponseEntity.ok().build());
    }

    @GetMapping("/owners")
    @Operation(summary = "Direct owners",
            description = "A person or a company may own this company. Ownership is a relationship of kind OWNERSHIP.")
    public Mono<ResponseEntity<Object>> owners(@PathVariable UUID partyId) {
        return partyFactsService.owners(partyId).map(ResponseEntity::ok);
    }

    @GetMapping("/subsidiaries")
    @Operation(summary = "Direct subsidiaries",
            description = "Companies this company owns, and children stored as PARENT.")
    public Mono<ResponseEntity<Object>> subsidiaries(@PathVariable UUID partyId) {
        return partyFactsService.subsidiaries(partyId).map(ResponseEntity::ok);
    }

    @GetMapping("/ancestors")
    @Operation(summary = "Ancestors", description = "Walks parent edges upward. Depth defaults to 25.")
    public Mono<ResponseEntity<Object>> ancestors(
            @PathVariable UUID partyId,
            @RequestParam(defaultValue = "25") int depth) {
        return partyFactsService.ancestors(partyId, depth).map(ResponseEntity::ok);
    }
}
