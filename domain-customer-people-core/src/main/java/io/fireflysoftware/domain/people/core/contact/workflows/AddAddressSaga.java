package io.fireflysoftware.domain.people.core.contact.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterAddressCommand;
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
 * Saga orchestrator for adding address to existing customers.
 * 
 * This orchestrator manages the distributed transaction for adding addresses to customers,
 * ensuring data consistency by coordinating the address registration step.
 * The step is designed to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_ADD_ADDRESS_NAME)
@Service
public class AddAddressSaga {

    private final CommandBus commandBus;

    @Autowired
    public AddAddressSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_ADDRESS)
    @StepEvent(type = EVENT_ADDRESS_REGISTERED)
    public Mono<UUID> registerAddress(RegisterAddressCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

}