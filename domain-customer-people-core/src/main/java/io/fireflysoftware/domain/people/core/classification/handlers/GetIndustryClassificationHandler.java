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


package io.fireflysoftware.domain.people.core.classification.handlers;

import io.fireflysoftware.core.customer.sdk.api.IndustryClassificationsApi;
import io.fireflysoftware.core.customer.sdk.model.PartyIndustryClassificationDTO;
import io.fireflysoftware.domain.people.core.classification.queries.IndustryClassificationQuery;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@QueryHandlerComponent
public class GetIndustryClassificationHandler
        extends QueryHandler<IndustryClassificationQuery, PartyIndustryClassificationDTO> {

    private final IndustryClassificationsApi industryClassificationsApi;

    public GetIndustryClassificationHandler(IndustryClassificationsApi industryClassificationsApi) {
        this.industryClassificationsApi = industryClassificationsApi;
    }

    @Override
    protected Mono<PartyIndustryClassificationDTO> doHandle(IndustryClassificationQuery query) {
        return industryClassificationsApi.get(query.partyId(), query.classificationId(), UUID.randomUUID().toString());
    }
}
