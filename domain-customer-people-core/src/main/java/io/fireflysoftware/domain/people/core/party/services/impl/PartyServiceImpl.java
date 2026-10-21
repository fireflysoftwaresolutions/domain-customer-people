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


package io.fireflysoftware.domain.people.core.party.services.impl;

import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.RemovePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.commands.UpdatePartyRelationshipCommand;
import io.fireflysoftware.domain.people.core.party.services.PartyService;
import io.fireflysoftware.domain.people.core.party.workflows.RegisterPartyRelationshipSaga;
import io.fireflysoftware.domain.people.core.party.workflows.UpdatePartyRelationshipSaga;
import io.fireflysoftware.domain.people.core.party.workflows.RemovePartyRelationshipSaga;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PartyServiceImpl implements PartyService {

    private final SagaEngine engine;

    @Autowired
    public PartyServiceImpl(SagaEngine engine) {
        this.engine = engine;
    }


    @Override
    public Mono<SagaResult> registerPartyRelationship(RegisterPartyRelationshipCommand command) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("registerPartyRelationship", command)
                .build();

        return engine.execute("RegisterPartyRelationshipSaga", inputs);
    }

    @Override
    public Mono<SagaResult> updatePartyRelationship(UpdatePartyRelationshipCommand command, UUID relationId) {
        UpdatePartyRelationshipCommand prepared = command.withPartyRelationshipId(relationId);
        StepInputs inputs = StepInputs.builder()
                .forStepId("readPartyRelationship", prepared)
                .forStepId("updatePartyRelationship", prepared)
                .build();

        return engine.execute("UpdatePartyRelationshipSaga", inputs);
    }

    @Override
    public Mono<SagaResult> removePartyRelationship(UUID relationId) {
        RemovePartyRelationshipCommand command = new RemovePartyRelationshipCommand(relationId);
        StepInputs inputs = StepInputs.builder()
                .forStepId("removePartyRelationship", command)
                .build();

        return engine.execute("RemovePartyRelationshipSaga", inputs);
    }

}
