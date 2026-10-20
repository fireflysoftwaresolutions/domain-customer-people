package io.fireflysoftware.domain.people.core.contact.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.contact.commands.UpdateEmailCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.*;


/**
 * Saga orchestrator for updating customer email processes.
 * 
 * This orchestrator manages the distributed transaction for updating customer emails,
 * coordinating the email update step. The step is designed
 * to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_UPDATE_EMAIL_NAME)
@Service
public class UpdateEmailSaga {

    private final CommandBus commandBus;

    @Autowired
    public UpdateEmailSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_UPDATE_EMAIL)
    @StepEvent(type = EVENT_EMAIL_UPDATED)
    public Mono<Void> updateEmail(UpdateEmailCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd).then();
    }

}