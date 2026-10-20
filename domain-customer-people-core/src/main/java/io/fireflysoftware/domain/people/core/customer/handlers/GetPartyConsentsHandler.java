package io.fireflysoftware.domain.people.core.customer.handlers;

import io.fireflysoftware.core.customer.sdk.api.ConsentsApi;
import io.fireflysoftware.core.customer.sdk.model.FilterRequestConsentDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.people.core.customer.queries.PartyConsentsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetPartyConsentsHandler extends QueryHandler<PartyConsentsQuery, PaginationResponse> {

    private final ConsentsApi consentsApi;

    @Override
    protected Mono<PaginationResponse> doHandle(PartyConsentsQuery query) {
        log.debug("Fetching consents for partyId={}", query.getPartyId());
        return consentsApi.filterConsents(query.getPartyId(), new FilterRequestConsentDTO(), UUID.randomUUID().toString());
    }
}
