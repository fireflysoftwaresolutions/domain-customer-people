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


package io.fireflysoftware.domain.people.core.status.services.impl;

import io.fireflysoftware.domain.people.core.status.commands.UpdateStatusCommand;
import io.fireflysoftware.domain.people.core.status.services.StatusService;
import io.fireflysoftware.domain.people.core.status.workflows.UpdateStatusSaga;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StatusServiceImpl implements StatusService {

    private final SagaEngine engine;

    @Autowired
    public StatusServiceImpl(SagaEngine engine) {
        this.engine = engine;
    }

    @Override
    public Mono<SagaResult> updateStatus(UpdateStatusCommand updateStatusCommand) {
        StepInputs inputs = StepInputs.builder()
                .forStepId("readOpenPartyStatus", updateStatusCommand)
                .forStepId("updateStatus", updateStatusCommand)
                .build();
        return engine.execute("UpdateStatusSaga", inputs);
    }
}