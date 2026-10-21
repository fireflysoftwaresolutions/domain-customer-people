package io.fireflysoftware.domain.people.core.hierarchy.handlers;

import io.fireflysoftware.core.customer.sdk.api.PartyHierarchyApi;
import io.fireflysoftware.core.customer.sdk.model.PartyAncestorDTO;
import io.fireflysoftware.domain.people.core.hierarchy.queries.PartyAncestorsQuery;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@QueryHandlerComponent
public class GetPartyAncestorsHandler extends QueryHandler<PartyAncestorsQuery, List<PartyAncestorDTO>> {

    private final PartyHierarchyApi partyHierarchyApi;

    public GetPartyAncestorsHandler(PartyHierarchyApi partyHierarchyApi) {
        this.partyHierarchyApi = partyHierarchyApi;
    }

    @Override
    protected Mono<List<PartyAncestorDTO>> doHandle(PartyAncestorsQuery query) {
        int depth = query.depth() <= 0 ? 25 : query.depth();
        return partyHierarchyApi.ancestors(query.partyId(), depth, UUID.randomUUID().toString()).collectList();
    }
}
