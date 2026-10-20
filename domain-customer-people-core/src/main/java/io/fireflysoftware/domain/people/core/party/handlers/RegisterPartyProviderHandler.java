package io.fireflysoftware.domain.people.core.party.handlers;

import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import io.fireflysoftware.core.customer.sdk.api.PartyProvidersApi;
import io.fireflysoftware.domain.people.core.party.commands.RegisterPartyProviderCommand;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterPartyProviderHandler extends CommandHandler<RegisterPartyProviderCommand, UUID> {

    private final PartyProvidersApi partyProvidersApi;

    public RegisterPartyProviderHandler(PartyProvidersApi partyProvidersApi) {
        this.partyProvidersApi = partyProvidersApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterPartyProviderCommand cmd) {

        return partyProvidersApi
                .createPartyProvider(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(partyProviderDTO ->
                        Objects.requireNonNull(Objects.requireNonNull(partyProviderDTO).getPartyProviderId()));
    }
}