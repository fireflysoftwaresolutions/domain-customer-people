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


package io.fireflysoftware.domain.people.core.compliance.workflows;

import io.fireflysoftware.core.customer.sdk.api.ConsentsApi;
import io.fireflysoftware.core.customer.sdk.model.ConsentDTO;
import io.fireflysoftware.domain.people.core.compliance.commands.UpdateConsentCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_CONSENT_UPDATED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_CONSENT_NAME;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_CONSENT;

/**
 * Updates a consent and restores the previous granted flag if a later step fails.
 */
@Saga(name = SAGA_UPDATE_CONSENT_NAME)
@Service
public class UpdateConsentSaga {

    private final CommandBus commandBus;
    private final ConsentsApi consentsApi;

    @Autowired
    public UpdateConsentSaga(CommandBus commandBus, ConsentsApi consentsApi) {
        this.commandBus = commandBus;
        this.consentsApi = consentsApi;
    }

    @SagaStep(id = "readConsent")
    @StepEvent(type = "consent.read")
    public Mono<ConsentDTO> readConsent(UpdateConsentCommand cmd, ExecutionContext ctx) {
        return consentsApi.getConsentById(cmd.partyId(), cmd.consentId(), UUID.randomUUID().toString())
                .doOnNext(previous -> ctx.putVariable("previousConsent", previous));
    }

    @SagaStep(id = STEP_UPDATE_CONSENT, dependsOn = "readConsent", compensate = "restoreConsent")
    @StepEvent(type = EVENT_CONSENT_UPDATED)
    public Mono<String> updateConsent(UpdateConsentCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd).thenReturn("updated");
    }

    public Mono<Void> restoreConsent(String ignored, ExecutionContext ctx) {
        ConsentDTO previous = ctx.getVariableAs("previousConsent", ConsentDTO.class);
        if (previous == null || previous.getGranted() == null || previous.getPartyId() == null || previous.getConsentId() == null) {
            return Mono.empty();
        }
        return commandBus.send(new UpdateConsentCommand(
                previous.getPartyId(),
                previous.getConsentId(),
                previous.getGranted(),
                previous.getApplicationId())).then();
    }
}
