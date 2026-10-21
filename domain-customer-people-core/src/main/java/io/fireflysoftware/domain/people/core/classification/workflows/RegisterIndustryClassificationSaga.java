package io.fireflysoftware.domain.people.core.classification.workflows;

import io.fireflysoftware.domain.people.core.classification.commands.RegisterPartyIndustryClassificationCommand;
import io.fireflysoftware.domain.people.core.classification.commands.RemovePartyIndustryClassificationCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Saga(name = "RegisterIndustryClassificationSaga")
@Service
public class RegisterIndustryClassificationSaga {

    private final CommandBus commandBus;

    public RegisterIndustryClassificationSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = "registerIndustryClassification", compensate = "removeIndustryClassification")
    @StepEvent(type = "industryClassification.registered")
    public Mono<UUID> registerIndustryClassification(RegisterPartyIndustryClassificationCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd)
                .doOnNext(id -> ctx.putVariable("partyId", cmd.getPartyId()));
    }

    public Mono<Void> removeIndustryClassification(UUID classificationId, ExecutionContext ctx) {
        if (classificationId == null) {
            return Mono.empty();
        }
        UUID partyId = ctx.getVariableAs("partyId", UUID.class);
        return commandBus.send(new RemovePartyIndustryClassificationCommand(partyId, classificationId));
    }
}
