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

import io.fireflysoftware.core.customer.sdk.api.LegalEntitiesApi;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.core.customer.sdk.api.PartiesApi;
import io.fireflysoftware.core.customer.sdk.model.PartyDTO;
import io.fireflysoftware.domain.people.core.customer.dto.CustomerSearchResultDTO;
import io.fireflysoftware.domain.people.core.customer.dto.EnrichedPartyDTO;
import io.fireflysoftware.domain.people.core.customer.queries.FilterCustomersQuery;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@QueryHandlerComponent
public class FilterCustomersHandler extends QueryHandler<FilterCustomersQuery, CustomerSearchResultDTO> {

    private final PartiesApi partiesApi;
    private final NaturalPersonsApi naturalPersonsApi;
    private final LegalEntitiesApi legalEntitiesApi;

    public FilterCustomersHandler(PartiesApi partiesApi,
                                  NaturalPersonsApi naturalPersonsApi,
                                  LegalEntitiesApi legalEntitiesApi) {
        this.partiesApi = partiesApi;
        this.naturalPersonsApi = naturalPersonsApi;
        this.legalEntitiesApi = legalEntitiesApi;
    }

    @Override
    protected Mono<CustomerSearchResultDTO> doHandle(FilterCustomersQuery query) {
        log.debug("Filtering customers with request={}", query.getFilterRequest());
        return partiesApi.filterParties(query.getFilterRequest(), idempotencyKey())
                .flatMap(page -> {
                    List<PartyDTO> rows = page.getContent() == null ? Collections.emptyList() : page.getContent();
                    return Flux.fromIterable(rows)
                            .flatMap(this::enrich)
                            .collectList()
                            .map(enriched -> CustomerSearchResultDTO.builder()
                                    .content(enriched)
                                    .totalElements(page.getTotalElements())
                                    .totalPages(page.getTotalPages())
                                    .currentPage(page.getCurrentPage())
                                    .build());
                });
    }

    private Mono<EnrichedPartyDTO> enrich(PartyDTO party) {
        EnrichedPartyDTO.EnrichedPartyDTOBuilder builder = EnrichedPartyDTO.builder().party(party);
        if (party.getPartyKind() == PartyDTO.PartyKindEnum.INDIVIDUAL) {
            return naturalPersonsApi.getNaturalPersonByPartyId(party.getPartyId(), idempotencyKey())
                    .map(np -> builder.naturalPerson(np).build())
                    .onErrorResume(ex -> {
                        log.warn("Natural-person lookup failed for partyId={}: {}", party.getPartyId(), ex.toString());
                        return Mono.just(builder.build());
                    })
                    .defaultIfEmpty(builder.build());
        }
        if (party.getPartyKind() == PartyDTO.PartyKindEnum.ORGANIZATION) {
            return legalEntitiesApi.getLegalEntityByPartyId(party.getPartyId(), idempotencyKey())
                    .map(le -> builder.legalEntity(le).build())
                    .onErrorResume(ex -> {
                        log.warn("Legal-entity lookup failed for partyId={}: {}", party.getPartyId(), ex.toString());
                        return Mono.just(builder.build());
                    })
                    .defaultIfEmpty(builder.build());
        }
        return Mono.just(builder.build());
    }

    private static String idempotencyKey() {
        return UUID.randomUUID().toString();
    }
}
