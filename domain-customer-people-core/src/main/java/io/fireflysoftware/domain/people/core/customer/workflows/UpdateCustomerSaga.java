package io.fireflysoftware.domain.people.core.customer.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.customer.commands.UpdateCustomerCommand;
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
 * Saga orchestrator for customer (natural person) update flows.
 *
 * Coordinates the single-step update of an existing natural person attached to a party.
 * Wrapping the call in a saga keeps the contract symmetric with RegisterCustomerSaga
 * and leaves room for additional compensatable steps (contact data, status entries, etc.)
 * without changing the public API. The legal-entity counterpart is handled by
 * {@link io.fireflysoftware.domain.people.core.business.workflows.UpdateBusinessSaga}.
 */
@Saga(name = SAGA_UPDATE_CUSTOMER_SAGA)
@Service
public class UpdateCustomerSaga {

    private final CommandBus commandBus;

    @Autowired
    public UpdateCustomerSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_UPDATE_CUSTOMER)
    @StepEvent(type = EVENT_CUSTOMER_CHANGED)
    public Mono<UUID> updateCustomer(UpdateCustomerCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }


}
