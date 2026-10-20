package io.fireflysoftware.domain.people.core.customer.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.NaturalPersonsApi;
import io.fireflysoftware.domain.people.core.customer.commands.RegisterNaturalPersonCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterNaturalPersonHandler extends CommandHandler<RegisterNaturalPersonCommand, UUID> {

    private final NaturalPersonsApi naturalPersonsApi;

    public RegisterNaturalPersonHandler(NaturalPersonsApi naturalPersonsApi) {
        this.naturalPersonsApi = naturalPersonsApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterNaturalPersonCommand cmd) {
        return naturalPersonsApi
                .createNaturalPerson(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(naturalPersonDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(naturalPersonDTO)).getNaturalPersonId());
    }
}