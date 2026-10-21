package io.fireflysoftware.domain.people.core.hierarchy.queries;

import io.fireflysoftware.core.customer.sdk.model.PartyRelationshipDTO;
import org.fireflyframework.cqrs.query.Query;

import java.util.List;
import java.util.UUID;

public record PartySubsidiariesQuery(UUID partyId) implements Query<List<PartyRelationshipDTO>> {
}
