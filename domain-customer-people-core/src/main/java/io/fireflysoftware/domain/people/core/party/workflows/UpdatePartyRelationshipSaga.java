package io.fireflysoftware.domain.people.core.party.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.party.commands.UpdatePartyRelationshipCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.*;

/**
 * Saga orchestrator for party relationship update processes.
 * 
 * This orchestrator manages the distributed transaction for updating party relationships,
 * coordinating the update of relationship information between parties.
 * Each step is designed to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_UPDATE_PARTY_RELATIONSHIP)
@Service
public class UpdatePartyRelationshipSaga {

    private final CommandBus commandBus;

    @Autowired
    public UpdatePartyRelationshipSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_UPDATE_PARTY_RELATIONSHIP)
    @StepEvent(type = EVENT_PARTY_RELATIONSHIP_UPDATED)
    public Mono<UUID> updatePartyRelationship(UpdatePartyRelationshipCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }
}