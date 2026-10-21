/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
