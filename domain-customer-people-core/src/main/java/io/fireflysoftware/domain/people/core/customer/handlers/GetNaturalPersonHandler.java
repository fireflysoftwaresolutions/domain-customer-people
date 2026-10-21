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

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import org.fireflyframework.web.error.exceptions.BusinessException;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.core.customer.sdk.model.NaturalPersonDTO;
import io.fireflysoftware.domain.people.core.customer.queries.NaturalPersonQuery;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@QueryHandlerComponent
public class GetNaturalPersonHandler extends QueryHandler<NaturalPersonQuery, NaturalPersonDTO> {

    private final NaturalPersonsApi naturalPersonsApi;

    public GetNaturalPersonHandler(NaturalPersonsApi naturalPersonsApi) {
        this.naturalPersonsApi = naturalPersonsApi;
    }

    @Override
    protected Mono<NaturalPersonDTO> doHandle(NaturalPersonQuery cmd) {
        return naturalPersonsApi.getNaturalPersonByPartyId(cmd.getPartyId(), UUID.randomUUID().toString())
                .onErrorMap(WebClientResponseException.NotFound.class, ex -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "NATURAL_PERSON_NOT_FOUND",
                        "Natural person not found for partyId: " + cmd.getPartyId()));
    }
}