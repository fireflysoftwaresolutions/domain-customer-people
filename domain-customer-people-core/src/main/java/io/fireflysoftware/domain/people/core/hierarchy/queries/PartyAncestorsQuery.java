package io.fireflysoftware.domain.people.core.hierarchy.queries;

import io.fireflysoftware.core.customer.sdk.model.PartyAncestorDTO;
import org.fireflyframework.cqrs.query.Query;

import java.util.List;
import java.util.UUID;

public record PartyAncestorsQuery(UUID partyId, int depth) implements Query<List<PartyAncestorDTO>> {
}
