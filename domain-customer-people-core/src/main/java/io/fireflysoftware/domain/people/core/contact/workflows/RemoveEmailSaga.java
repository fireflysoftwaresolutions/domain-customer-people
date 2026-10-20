package io.fireflysoftware.domain.people.core.contact.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.contact.commands.RemoveEmailCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.*;

/**
 * Saga orchestrator for removing emails from existing customers.
 * 
 * This orchestrator manages the distributed transaction for removing customer emails,
 * ensuring data consistency by coordinating the email removal step.
 * The step is designed to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_REMOVE_EMAIL_NAME)
@Service
public class RemoveEmailSaga {

    private final CommandBus commandBus;

    @Autowired
    public RemoveEmailSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REMOVE_EMAIL)
    @StepEvent(type = EVENT_EMAIL_REMOVED)
    public Mono<Void> removeEmail(RemoveEmailCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

}