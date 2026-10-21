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


package io.fireflysoftware.domain.people.core.attribute.workflows;

import io.fireflysoftware.domain.people.core.attribute.commands.RegisterPartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.RemovePartyAttributeCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Saga(name = "RegisterPartyAttributeSaga")
@Service
public class RegisterPartyAttributeSaga {

    private final CommandBus commandBus;

    public RegisterPartyAttributeSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = "registerPartyAttribute", compensate = "removePartyAttribute")
    @StepEvent(type = "partyAttribute.registered")
    public Mono<UUID> registerPartyAttribute(RegisterPartyAttributeCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd)
                .doOnNext(id -> ctx.putVariable("partyId", cmd.getPartyId()));
    }

    public Mono<Void> removePartyAttribute(UUID attributeId, ExecutionContext ctx) {
        if (attributeId == null) {
            return Mono.empty();
        }
        UUID partyId = ctx.getVariableAs("partyId", UUID.class);
        return commandBus.send(new RemovePartyAttributeCommand(partyId, attributeId));
    }
}
