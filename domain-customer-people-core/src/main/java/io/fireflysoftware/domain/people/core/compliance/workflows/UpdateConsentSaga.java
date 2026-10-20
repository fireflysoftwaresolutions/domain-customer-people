package io.fireflysoftware.domain.people.core.compliance.workflows;

import io.fireflysoftware.domain.people.core.compliance.commands.UpdateConsentCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.EVENT_CONSENT_UPDATED;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.SAGA_UPDATE_CONSENT_NAME;
import static io.fireflysoftware.domain.people.core.utils.constants.RegisterCustomerConstants.STEP_UPDATE_CONSENT;

/**
 * Saga orchestrator for updating an existing customer consent record.
 * <p>
 * Wraps the {@link UpdateConsentCommand} dispatch in a single-step saga so the
 * operation participates in the platform's distributed-transaction tooling and
 * emits the {@code consent.updated} domain event consistently with other
 * compliance flows.
 */
@Saga(name = SAGA_UPDATE_CONSENT_NAME)
@Service
public class UpdateConsentSaga {

    private final CommandBus commandBus;

    @Autowired
    public UpdateConsentSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP_UPDATE_CONSENT)
    @StepEvent(type = EVENT_CONSENT_UPDATED)
    public Mono<String> updateConsent(UpdateConsentCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd).then(Mono.just("updated"));
    }
}
