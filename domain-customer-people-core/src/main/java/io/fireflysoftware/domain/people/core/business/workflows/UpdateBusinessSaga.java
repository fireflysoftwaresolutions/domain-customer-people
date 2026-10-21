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


package io.fireflysoftware.domain.people.core.business.workflows;

import io.fireflysoftware.core.customer.sdk.model.LegalEntityDTO;
import io.fireflysoftware.domain.people.core.business.commands.UpdateBusinessCommand;
import io.fireflysoftware.domain.people.core.customer.queries.LegalEntityQuery;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_BUSINESS_CHANGED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_BUSINESS_SAGA;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_BUSINESS;

/**
 * Updates a legal entity and restores the previous record if a later step fails.
 * Industry is not a column on the entity. Callers store a scheme and a code
 * through the industry-classification command.
 */
@Saga(name = SAGA_UPDATE_BUSINESS_SAGA)
@Service
public class UpdateBusinessSaga {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @Autowired
    public UpdateBusinessSaga(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @SagaStep(id = "readBusiness")
    @StepEvent(type = "business.read")
    public Mono<LegalEntityDTO> readBusiness(UpdateBusinessCommand cmd, ExecutionContext ctx) {
        return queryBus.query(LegalEntityQuery.builder().partyId(cmd.getPartyId()).build())
                .doOnNext(previous -> ctx.putVariable("previousBusiness", previous));
    }

    @SagaStep(id = STEP_UPDATE_BUSINESS, dependsOn = "readBusiness", compensate = "restoreBusiness")
    @StepEvent(type = EVENT_BUSINESS_CHANGED)
    public Mono<UUID> updateBusiness(UpdateBusinessCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restoreBusiness(UUID legalEntityId, ExecutionContext ctx) {
        LegalEntityDTO previous = ctx.getVariableAs("previousBusiness", LegalEntityDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        UpdateBusinessCommand restore = new UpdateBusinessCommand();
        BeanUtils.copyProperties(previous, restore);
        return commandBus.send(restore).then();
    }
}
