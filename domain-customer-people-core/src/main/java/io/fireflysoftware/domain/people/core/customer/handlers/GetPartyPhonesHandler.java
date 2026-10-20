package io.fireflysoftware.domain.people.core.customer.handlers;

import io.fireflysoftware.core.customer.sdk.api.PhoneContactsApi;
import io.fireflysoftware.core.customer.sdk.model.FilterRequestPhoneContactDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponsePhoneContactDTO;
import io.fireflysoftware.domain.people.core.customer.queries.PartyPhonesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetPartyPhonesHandler extends QueryHandler<PartyPhonesQuery, PaginationResponsePhoneContactDTO> {

    private final PhoneContactsApi phoneContactsApi;

    @Override
    protected Mono<PaginationResponsePhoneContactDTO> doHandle(PartyPhonesQuery query) {
        log.debug("Fetching phone contacts for partyId={}", query.getPartyId());
        return phoneContactsApi.filterPhoneContacts(query.getPartyId(), new FilterRequestPhoneContactDTO(), UUID.randomUUID().toString());
    }
}
