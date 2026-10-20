package io.fireflysoftware.domain.people.core.contact.workflows;

import org.fireflyframework.cqrs.command.CommandBus;
import io.fireflysoftware.domain.people.core.contact.commands.UpdatePhoneCommand;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.*;


/**
 * Saga orchestrator for updating customer phone processes.
 * 
 * This orchestrator manages the distributed transaction for updating customer phones,
 * coordinating the phone update step. The step is designed
 * to be compensatable to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_UPDATE_PHONE_NAME)
@Service
public class UpdatePhoneSaga {

    private final CommandBus commandBus;

    @Autowired
    public UpdatePhoneSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_UPDATE_PHONE)
    @StepEvent(type = EVENT_PHONE_UPDATED)
    public Mono<Void> updatePhone(UpdatePhoneCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd).then();
    }

}