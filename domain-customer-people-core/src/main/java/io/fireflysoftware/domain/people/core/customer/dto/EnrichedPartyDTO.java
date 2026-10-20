package io.fireflysoftware.domain.people.core.customer.dto;

import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import io.fireflysoftware.core.customer.sdk.model.PartyDTO;
import lombok.Builder;
import lombok.Data;

/**
 * A {@link PartyDTO} enriched with its associated natural-person or legal-entity
 * record. Exactly one of the two association fields is populated for a given
 * party (based on {@code party.partyKind}); the other is {@code null}. Both
 * may be {@code null} if the enrichment lookup failed or no association exists
 * yet — the bare {@code party} is always preserved.
 */
@Data
@Builder
public class EnrichedPartyDTO {

    private PartyDTO party;
    private NaturalPersonDTO naturalPerson;
    private LegalEntityDTO legalEntity;
}
