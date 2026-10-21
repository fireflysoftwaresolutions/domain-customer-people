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
