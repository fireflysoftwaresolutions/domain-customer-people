package io.fireflysoftware.domain.people.core.hierarchy.handlers;

import io.fireflysoftware.core.customer.sdk.api.PartyHierarchyApi;
import io.fireflysoftware.core.customer.sdk.model.PartyRelationshipDTO;
import io.fireflysoftware.domain.people.core.hierarchy.queries.PartySubsidiariesQuery;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@QueryHandlerComponent
public class GetPartySubsidiariesHandler extends QueryHandler<PartySubsidiariesQuery, List<PartyRelationshipDTO>> {

    private final PartyHierarchyApi partyHierarchyApi;

    public GetPartySubsidiariesHandler(PartyHierarchyApi partyHierarchyApi) {
        this.partyHierarchyApi = partyHierarchyApi;
    }

    @Override
    protected Mono<List<PartyRelationshipDTO>> doHandle(PartySubsidiariesQuery query) {
        return partyHierarchyApi.directSubsidiaries(query.partyId(), UUID.randomUUID().toString()).collectList();
    }
}
