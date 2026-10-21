package io.fireflysoftware.domain.people.core.party.workflows;

import io.fireflysoftware.core.customer.sdk.api.PartyRelationshipsApi;
import io.fireflysoftware.core.customer.sdk.model.PartyRelationshipDTO;
import io.fireflysoftware.domain.people.core.party.commands.UpdatePartyRelationshipCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_PARTY_RELATIONSHIP_UPDATED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_PARTY_RELATIONSHIP;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_PARTY_RELATIONSHIP;

/**
 * Updates an ownership, control, parent, or officer relationship and restores
 * the previous edge if a later step fails. A person or a company may own a company.
 */
@Saga(name = SAGA_UPDATE_PARTY_RELATIONSHIP)
@Service
public class UpdatePartyRelationshipSaga {

    private final CommandBus commandBus;
    private final PartyRelationshipsApi partyRelationshipsApi;

    @Autowired
    public UpdatePartyRelationshipSaga(CommandBus commandBus, PartyRelationshipsApi partyRelationshipsApi) {
        this.commandBus = commandBus;
        this.partyRelationshipsApi = partyRelationshipsApi;
    }

    @SagaStep(id = "readPartyRelationship")
    @StepEvent(type = "partyrelationship.read")
    public Mono<PartyRelationshipDTO> readPartyRelationship(UpdatePartyRelationshipCommand cmd, ExecutionContext ctx) {
        return partyRelationshipsApi.getPartyRelationshipById(cmd.getPartyRelationshipId(), UUID.randomUUID().toString())
                .doOnNext(previous -> ctx.putVariable("previousRelationship", previous));
    }

    @SagaStep(id = STEP_UPDATE_PARTY_RELATIONSHIP, dependsOn = "readPartyRelationship", compensate = "restorePartyRelationship")
    @StepEvent(type = EVENT_PARTY_RELATIONSHIP_UPDATED)
    public Mono<UUID> updatePartyRelationship(UpdatePartyRelationshipCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

    public Mono<Void> restorePartyRelationship(UUID relationshipId, ExecutionContext ctx) {
        PartyRelationshipDTO previous = ctx.getVariableAs("previousRelationship", PartyRelationshipDTO.class);
        if (previous == null) {
            return Mono.empty();
        }
        UpdatePartyRelationshipCommand restore = new UpdatePartyRelationshipCommand();
        BeanUtils.copyProperties(previous, restore);
        return commandBus.send(restore).then();
    }
}
