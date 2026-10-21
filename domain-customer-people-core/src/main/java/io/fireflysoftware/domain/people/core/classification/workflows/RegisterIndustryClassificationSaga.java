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


package io.fireflysoftware.domain.people.core.classification.workflows;

import io.fireflysoftware.domain.people.core.classification.commands.RegisterPartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RemovePartyIndustryClassificationCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Saga(name = "RegisterIndustryClassificationSaga")
@Service
public class RegisterIndustryClassificationSaga {

    private final CommandBus commandBus;

    public RegisterIndustryClassificationSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = "registerIndustryClassification", compensate = "removeIndustryClassification")
    @StepEvent(type = "industryClassification.registered")
    public Mono<UUID> registerIndustryClassification(RegisterPartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd)
                .doOnNext(id -> ctx.putVariable("partyId", cmd.getPartyId()));
    }

    public Mono<Void> removeIndustryClassification(UUID classificationId, ExecutionContext ctx) {
        if (classificationId == null) {
            return Mono.empty();
        }
        UUID partyId = ctx.getVariableAs("partyId", UUID.class);
        return commandBus.send(new RemovePartyIndustryClassificationCommand(partyId, classificationId));
    }
}
