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

import io.fireflysoftware.core.customer.sdk.model.PartyAttributeDTO;
import io.fireflysoftware.domain.people.core.attribute.commands.RegisterPartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.RemovePartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.queries.PartyAttributeQuery;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Saga(name = "RemovePartyAttributeSaga")
@Service
public class RemovePartyAttributeSaga {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public RemovePartyAttributeSaga(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @SagaStep(id = "readPartyAttributeForDelete")
    @StepEvent(type = "partyAttribute.read")
    public Mono<PartyAttributeDTO> readPartyAttribute(RemovePartyAttributeCommand cmd, ExecutionContext ctx) {
        return queryBus.query(new PartyAttributeQuery(cmd.partyId(), cmd.attributeId()))
                .doOnNext(previous -> ctx.putVariable("previousAttribute", previous));
    }

    @SagaStep(id = "removePartyAttribute", dependsOn = "readPartyAttributeForDelete", compensate = "restoreRemovedPartyAttribute")
    @StepEvent(type = "partyAttribute.removed")
    public Mono<Void> removePartyAttribute(RemovePartyAttributeCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restoreRemovedPartyAttribute(Void ignored, ExecutionContext ctx) {
        PartyAttributeDTO previous = ctx.getVariableAs("previousAttribute", PartyAttributeDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        RegisterPartyAttributeCommand restore = new RegisterPartyAttributeCommand();
        BeanUtils.copyProperties(previous, restore);
        return commandBus.send(restore).then();
    }
}
