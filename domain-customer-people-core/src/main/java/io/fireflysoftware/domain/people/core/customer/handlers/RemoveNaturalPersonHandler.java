package io.fireflysoftware.domain.people.core.customer.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.domain.people.core.customer.commands.RemoveNaturalPersonCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class RemoveNaturalPersonHandler extends CommandHandler<RemoveNaturalPersonCommand, Void> {

    private final NaturalPersonsApi naturalPersonsApi;

    public RemoveNaturalPersonHandler(NaturalPersonsApi naturalPersonsApi) {
        this.naturalPersonsApi = naturalPersonsApi;
    }

    @Override
    protected Mono<Void> doHandle(RemoveNaturalPersonCommand cmd) {
        return naturalPersonsApi.deleteNaturalPerson(cmd.partyId(), cmd.naturalPersonId(), UUID.randomUUID().toString()).then();
    }
}