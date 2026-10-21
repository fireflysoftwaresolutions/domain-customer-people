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


package io.fireflysoftware.domain.people.core.business.services.impl;

import org.fireflyframework.cqrs.query.QueryBus;
import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.domain.people.core.business.commands.RegisterBusinessCommand;
import io.fireflysoftware.domain.people.core.business.commands.UpdateBusinessCommand;
import io.fireflysoftware.domain.people.core.business.services.BusinessService;
import io.fireflysoftware.domain.people.core.business.workflows.UpdateBusinessSaga;
import io.fireflysoftware.domain.people.core.customer.queries.LegalEntityQuery;
import io.fireflysoftware.domain.people.core.customer.workflows.RegisterCustomerSaga;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.ExpandEach;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class BusinessServiceImpl implements BusinessService {

    private final SagaEngine engine;
    private final QueryBus queryBus;

    @Autowired
    public BusinessServiceImpl(SagaEngine engine, QueryBus queryBus) {
        this.engine = engine;
        this.queryBus = queryBus;
    }


    @Override
    public Mono<SagaResult> registerBusiness(RegisterBusinessCommand command) {
        // Defensively default the optional collections so callers (notably the experience tier)
        // can post a minimal RegisterBusinessCommand with only party, legalEntity and any
        // subset of contact channels, without tripping ExpandEach.of(...) on null. Each
        // collection is independent and optional for the saga's per-step expansion.
        StepInputs inputs = StepInputs.builder()
                .forStepId("registerParty", command.party())
                .forStepId("registerLegalEntity", command.legalEntity())
                .forStepId("registerStatusEntry", ExpandEach.of(nullSafe(command.statusHistory())))
                .forStepId("registerIdentityDocument", ExpandEach.of(nullSafe(command.identityDocuments())))
                .forStepId("registerAddress", ExpandEach.of(nullSafe(command.addresses())))
                .forStepId("registerEmail", ExpandEach.of(nullSafe(command.emails())))
                .forStepId("registerPhone", ExpandEach.of(nullSafe(command.phones())))
                .forStepId("registerEconomicActivityLink", ExpandEach.of(nullSafe(command.economicActivities())))
                .forStepId("registerPartyProvider", ExpandEach.of(nullSafe(command.providers())))
                .forStepId("registerPartyRelationship", ExpandEach.of(nullSafe(command.relationships())))
                .forStepId("registerPartyGroupMembership", ExpandEach.of(nullSafe(command.groupMemberships())))
                .forStepId("registerIndustryClassification", ExpandEach.of(nullSafe(command.industryClassifications())))
                .forStepId("registerPartyAttribute", ExpandEach.of(nullSafe(command.attributes())))
                .build();

        return engine.execute("RegisterCustomerSaga", inputs);
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
    }

    @Override
    public Mono<SagaResult> updateBusiness(UpdateBusinessCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("readBusiness", command)
                .forStepId("updateBusiness", command)
                .build();

        return engine.execute("UpdateBusinessSaga", inputs);
    }

    @Override
    public Mono<LegalEntityDTO> getBusinessInfo(UUID businessId) {
        return queryBus.query(LegalEntityQuery.builder().partyId(businessId).build());
    }
}
