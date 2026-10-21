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

import io.fireflysoftware.core.customer.sdk.model.PartyIndustryClassificationDTO;
import io.fireflysoftware.domain.people.core.classification.commands.UpdatePartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.queries.IndustryClassificationQuery;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Saga(name = "UpdateIndustryClassificationSaga")
@Service
public class UpdateIndustryClassificationSaga {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public UpdateIndustryClassificationSaga(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @SagaStep(id = "readIndustryClassification")
    @StepEvent(type = "industryClassification.read")
    public Mono<PartyIndustryClassificationDTO> readIndustryClassification(
            UpdatePartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return queryBus.query(new IndustryClassificationQuery(cmd.getPartyId(), cmd.classificationId()))
                .doOnNext(previous -> ctx.putVariable("previousClassification", previous));
    }

    @SagaStep(id = "updateIndustryClassification", dependsOn = "readIndustryClassification", compensate = "restoreIndustryClassification")
    @StepEvent(type = "industryClassification.updated")
    public Mono<UUID> updateIndustryClassification(UpdatePartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restoreIndustryClassification(UUID classificationId, ExecutionContext ctx) {
        PartyIndustryClassificationDTO previous = ctx.getVariableAs("previousClassification", PartyIndustryClassificationDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        UpdatePartyIndustryClassificationCommand restore = new UpdatePartyIndustryClassificationCommand();
        BeanUtils.copyProperties(previous, restore);
        return commandBus.send(restore).then();
    }
}
