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

import io.fireflysoftware.core.customer.sdk.api.EmailContactsApi;
import io.fireflysoftware.core.customer.sdk.model.FilterRequestEmailContactDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponseEmailContactDTO;
import io.fireflysoftware.domain.people.core.customer.queries.PartyEmailsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetPartyEmailsHandler extends QueryHandler<PartyEmailsQuery, PaginationResponseEmailContactDTO> {

    private final EmailContactsApi emailContactsApi;

    @Override
    protected Mono<PaginationResponseEmailContactDTO> doHandle(PartyEmailsQuery query) {
        log.debug("Fetching email contacts for partyId={}", query.getPartyId());
        return emailContactsApi.filterEmailContacts(query.getPartyId(), new FilterRequestEmailContactDTO(), UUID.randomUUID().toString());
    }
}
