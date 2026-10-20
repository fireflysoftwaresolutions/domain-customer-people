package io.fireflysoftware.domain.people.core.contact.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateAddressCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.*;


/**
 * Saga orchestrator for updating customer address processes.
 * 
 * This orchestrator manages the distributed transaction for updating customer addresses,
 * coordinating the address update step. The step is designed
 * to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_UPDATE_ADDRESS_NAME)
@Service
public class UpdateAddressSaga {

    private final CommandBus commandBus;

    @Autowired
    public UpdateAddressSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_UPDATE_ADDRESS)
    @StepEvent(type = EVENT_ADDRESS_UPDATED)
    public Mono<Void> updateAddress(UpdateAddressCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd).then();
    }

}