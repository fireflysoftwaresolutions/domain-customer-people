package io.fireflysoftware.domain.people.core.hierarchy.handlers;

import io.fireflysoftware.core.customer.sdk.api.PartyHierarchyApi;
import io.fireflysoftware.core.customer.sdk.model.PartyAncestorDTO;
import io.fireflysoftware.core.customer.sdk.model.PartyRelationshipDTO;
import io.fireflysoftware.domain.people.core.hierarchy.queries.PartyAncestorsQuery;
import io.fireflysoftware.domain.people.core.hierarchy.queries.PartyOwnersQuery;
import io.fireflysoftware.domain.people.core.hierarchy.queries.PartySubsidiariesQuery;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@QueryHandlerComponent
public class GetPartyOwnersHandler extends QueryHandler<PartyOwnersQuery, List<PartyRelationshipDTO>> {

    private final PartyHierarchyApi partyHierarchyApi;

    public GetPartyOwnersHandler(PartyHierarchyApi partyHierarchyApi) {
        this.partyHierarchyApi = partyHierarchyApi;
    }

    @Override
    protected Mono<List<PartyRelationshipDTO>> doHandle(PartyOwnersQuery query) {
        return partyHierarchyApi.directOwners(query.partyId(), UUID.randomUUID().toString()).collectList();
    }
}
