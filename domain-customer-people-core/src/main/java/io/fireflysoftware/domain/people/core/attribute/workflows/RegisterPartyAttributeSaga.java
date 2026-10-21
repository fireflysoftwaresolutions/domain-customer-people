package io.fireflysoftware.domain.people.core.attribute.workflows;

import io.fireflysoftware.domain.people.core.attribute.commands.RegisterPartyAttributeCommand;
import io.fireflysoftware.domain.people.core.attribute.commands.RemovePartyAttributeCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Saga(name = "RegisterPartyAttributeSaga")
@Service
public class RegisterPartyAttributeSaga {

    private final CommandBus commandBus;

    public RegisterPartyAttributeSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = "registerPartyAttribute", compensate = "removePartyAttribute")
    @StepEvent(type = "partyAttribute.registered")
    public Mono<UUID> registerPartyAttribute(RegisterPartyAttributeCommand cmd, ExecutionContext ctx) {
        return commandBus.send(cmd)
                .doOnNext(id -> ctx.putVariable("partyId", cmd.getPartyId()));
    }

    public Mono<Void> removePartyAttribute(UUID attributeId, ExecutionContext ctx) {
        if (attributeId == null) {
            return Mono.empty();
        }
        UUID partyId = ctx.getVariableAs("partyId", UUID.class);
        return commandBus.send(new RemovePartyAttributeCommand(partyId, attributeId));
    }
}
