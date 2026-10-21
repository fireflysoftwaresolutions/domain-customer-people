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


package io.fireflysoftware.domain.people.core.status.workflows;

import io.fireflysoftware.core.customer.sdk.model.FilterRequestPartyStatusDTO;
import io.fireflysoftware.core.customer.sdk.model.PaginationRequest;
import io.fireflysoftware.core.customer.sdk.model.PaginationResponse;
import io.fireflysoftware.core.customer.sdk.model.PartyStatusDTO;
import io.fireflysoftware.domain.people.core.status.commands.RegisterPartyStatusEntryCommand;
import io.fireflysoftware.domain.people.core.status.commands.RemovePartyStatusEntryCommand;
import io.fireflysoftware.domain.people.core.status.commands.UpdateStatusCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_STATUS_UPDATED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_STATUS;

/**
 * Changes party status through the customer core. The core closes the open status
 * row and opens a new one. Compensation deletes that new history row and opens
 * the previous status again.
 */
@Saga(name = "UpdateStatusSaga")
@Service
public class UpdateStatusSaga {

    private static final String PREVIOUS = "previousPartyStatus";

    private final CommandBus commandBus;
    private final io.fireflysoftware.core.customer.sdk.api.PartyStatusesApi partyStatusesApi;

    @Autowired
    public UpdateStatusSaga(CommandBus commandBus,
                            io.fireflysoftware.core.customer.sdk.api.PartyStatusesApi partyStatusesApi) {
        this.commandBus = commandBus;
        this.partyStatusesApi = partyStatusesApi;
    }

    @SagaStep(id = "readOpenPartyStatus")
    @StepEvent(type = "status.read")
    public Mono<PartyStatusDTO> readOpenPartyStatus(UpdateStatusCommand cmd, ExecutionContext ctx) {
        FilterRequestPartyStatusDTO filter = new FilterRequestPartyStatusDTO()
                .pagination(new PaginationRequest().pageNumber(0).pageSize(50));
        return partyStatusesApi.filterPartyStatuses(cmd.getPartyId(), filter, UUID.randomUUID().toString())
                .map(page -> openStatus(page, cmd.getPartyId()))
                .doOnNext(previous -> ctx.putVariable(PREVIOUS, previous));
    }

    @SagaStep(id = STEP_UPDATE_STATUS, dependsOn = "readOpenPartyStatus", compensate = "compensateStatusChange")
    @StepEvent(type = EVENT_STATUS_UPDATED)
    public Mono<UUID> updateStatus(UpdateStatusCommand cmd, ExecutionContext ctx) {
        ctx.putVariable("statusPartyId", cmd.getPartyId());
        return commandBus.send(cmd);
    }

    public Mono<Void> compensateStatusChange(UUID newStatusId, ExecutionContext ctx) {
        if (newStatusId == null) {
            return Mono.empty();
        }
        UUID partyId = ctx.getVariableAs("statusPartyId", UUID.class);
        PartyStatusDTO previous = ctx.getVariableAs(PREVIOUS, PartyStatusDTO.class);
        Mono<Void> deleteNew = commandBus.send(new RemovePartyStatusEntryCommand(partyId, newStatusId));
        if (previous == null || previous.getStatusCode() == null) {
            return deleteNew;
        }
        RegisterPartyStatusEntryCommand restore = new RegisterPartyStatusEntryCommand();
        restore.setPartyId(partyId);
        restore.setStatusCode(previous.getStatusCode());
        restore.setStatusReason(previous.getStatusReason());
        return deleteNew.then(commandBus.send(restore)).then();
    }

    private static PartyStatusDTO openStatus(PaginationResponse page, UUID partyId) {
        if (page == null || page.getContent() == null) {
            return new PartyStatusDTO().partyId(partyId);
        }
        return page.getContent().stream()
                .filter(PartyStatusDTO.class::isInstance)
                .map(PartyStatusDTO.class::cast)
                .filter(status -> status.getValidTo() == null)
                .findFirst()
                .orElseGet(() -> new PartyStatusDTO().partyId(partyId));
    }
}
