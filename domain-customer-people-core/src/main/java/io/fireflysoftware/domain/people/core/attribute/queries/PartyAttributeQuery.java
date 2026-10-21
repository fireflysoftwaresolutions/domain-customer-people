package io.fireflysoftware.domain.people.core.attribute.queries;

import io.fireflysoftware.core.customer.sdk.model.PartyAttributeDTO;
import org.fireflyframework.cqrs.query.Query;

import java.util.UUID;

public record PartyAttributeQuery(UUID partyId, UUID attributeId) implements Query<PartyAttributeDTO> {
}
