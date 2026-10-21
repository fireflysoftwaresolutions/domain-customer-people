package io.fireflysoftware.domain.people.core.attribute.handlers;

import io.fireflysoftware.core.customer.sdk.api.PartyAttributesApi;
import io.fireflysoftware.core.customer.sdk.model.PartyAttributeDTO;
import io.fireflysoftware.domain.people.core.attribute.queries.PartyAttributeQuery;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@QueryHandlerComponent
public class GetPartyAttributeHandler extends QueryHandler<PartyAttributeQuery, PartyAttributeDTO> {

    private final PartyAttributesApi partyAttributesApi;

    public GetPartyAttributeHandler(PartyAttributesApi partyAttributesApi) {
        this.partyAttributesApi = partyAttributesApi;
    }

    @Override
    protected Mono<PartyAttributeDTO> doHandle(PartyAttributeQuery query) {
        return partyAttributesApi.get1(query.partyId(), query.attributeId(), UUID.randomUUID().toString());
    }
}
