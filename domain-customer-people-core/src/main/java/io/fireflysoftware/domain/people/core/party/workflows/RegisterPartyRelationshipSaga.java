package io.fireflysoftware.domain.people.core.party.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyRelationshipCommand;
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
 * Saga orchestrator for customer registration processes.
 * 
 * This orchestrator manages the distributed transaction for registering customers,
 * coordinating multiple steps including party creation, person details registration,
 * contact information setup, and relationship establishment. Each step is designed
 * to be compensatable to ensure data consistency in case of failures.
 * 
 * The orchestrator handles both natural persons and legal entities, with conditional
 * logic to process only relevant information based on the customer type.
 */
@Saga(name = SAGA_REGISTER_PARTY_RELATIONSHIP)
@Service
public class RegisterPartyRelationshipSaga {

    private final CommandBus commandBus;

    @Autowired
    public RegisterPartyRelationshipSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_PARTY_RELATIONSHIP)
    @StepEvent(type = EVENT_PARTY_RELATIONSHIP_REGISTERED)
    public Mono<UUID> registerPartyRelationship(RegisterPartyRelationshipCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }
}
