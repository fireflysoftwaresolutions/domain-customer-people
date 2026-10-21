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


package io.fireflysoftware.domain.people.core.compliance.services.impl;

import io.fireflysoftware.domain.people.core.compliance.commands.UpdateConsentCommand;
import io.fireflysoftware.domain.people.core.compliance.services.ConsentService;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_CONSENT_NAME;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_CONSENT;

@Service
public class ConsentServiceImpl implements ConsentService {

    private final SagaEngine engine;

    @Autowired
    public ConsentServiceImpl(SagaEngine engine) {
        this.engine = engine;
    }

    @Override
    public Mono<Void> updateConsent(UUID partyId, UUID consentId, UpdateConsentCommand command) {
        UpdateConsentCommand merged = new UpdateConsentCommand(
                partyId,
                consentId,
                command.granted(),
                command.applicationId()
        );

        StepInputs inputs = StepInputs.builder()
                .forStepId("readConsent", merged)
                .forStepId(STEP_UPDATE_CONSENT, merged)
                .build();

        return engine.execute(SAGA_UPDATE_CONSENT_NAME, inputs).then();
    }
}
