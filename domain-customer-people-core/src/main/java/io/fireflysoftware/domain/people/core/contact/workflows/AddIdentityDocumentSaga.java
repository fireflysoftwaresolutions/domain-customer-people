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


package io.fireflysoftware.domain.people.core.contact.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterIdentityDocumentCommand;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveIdentityDocumentCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.GlobalConstants.CTX_PARTY_ID;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.*;

/**
 * Saga orchestrator for adding identity document to existing customers.
 * 
 * This orchestrator manages the distributed transaction for adding identity documents to customers,
 * ensuring data consistency by coordinating the identity document registration step.
 * The step is designed to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_ADD_IDENTITY_DOCUMENT_NAME)
@Service
public class AddIdentityDocumentSaga {

    private final CommandBus commandBus;

    @Autowired
    public AddIdentityDocumentSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_IDENTITY_DOCUMENT, compensate = COMPENSATE_REMOVE_IDENTITY_DOCUMENT)
    @StepEvent(type = EVENT_IDENTITY_DOCUMENT_REGISTERED)
    public Mono<UUID> registerIdentityDocument(RegisterIdentityDocumentCommand cmd, ExecutionContext ctx) {
        if (cmd.getPartyId() != null) {
            ctx.putVariable(CTX_PARTY_ID, cmd.getPartyId());
        }
        return commandBus.send(cmd);
    }

    public Mono<Void> removeIdentityDocument(UUID identityDocumentId, ExecutionContext ctx) {
        UUID partyId = (UUID) ctx.getVariable(CTX_PARTY_ID);
        if (identityDocumentId == null || partyId == null) {
            return Mono.empty();
        }
        return commandBus.send(new RemoveIdentityDocumentCommand(partyId, identityDocumentId));
    }

}