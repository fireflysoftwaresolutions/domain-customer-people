package io.fireflysoftware.domain.people.core.contact.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.contact.commands.RegisterEmailCommand;
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
 * Saga orchestrator for adding email to existing customers.
 * 
 * This orchestrator manages the distributed transaction for adding emails to customers,
 * ensuring data consistency by coordinating the email registration step.
 * The step is designed to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_ADD_EMAIL_NAME)
@Service
public class AddEmailSaga {

    private final CommandBus commandBus;

    @Autowired
    public AddEmailSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_REGISTER_EMAIL)
    @StepEvent(type = EVENT_EMAIL_REGISTERED)
    public Mono<UUID> registerEmail(RegisterEmailCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd);
    }

}