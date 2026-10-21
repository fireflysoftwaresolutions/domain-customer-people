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
import io.fireflysoftware.domain.people.core.contact.commands.RemoveIdentityDocumentCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.*;

/**
 * Saga orchestrator for removing identity documents from existing customers.
 * 
 * This orchestrator manages the distributed transaction for removing customer identity documents,
 * ensuring data consistency by coordinating the identity document removal step.
 * The step is designed to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_REMOVE_IDENTITY_DOCUMENT_NAME)
@Service
public class RemoveIdentityDocumentSaga {

    private final CommandBus commandBus;

    @Autowired
    public RemoveIdentityDocumentSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REMOVE_IDENTITY_DOCUMENT)
    @StepEvent(type = EVENT_IDENTITY_DOCUMENT_REMOVED)
    public Mono<Void> removeIdentityDocument(RemoveIdentityDocumentCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

}