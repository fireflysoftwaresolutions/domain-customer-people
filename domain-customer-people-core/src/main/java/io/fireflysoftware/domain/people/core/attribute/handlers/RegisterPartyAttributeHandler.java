package io.fireflysoftware.domain.people.core.attribute.handlers;

import io.fireflysoftware.core.customer.sdk.api.PartyAttributesApi;
import io.fireflysoftware.domain.people.core.attribute.commands.RegisterPartyAttributeCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class RegisterPartyAttributeHandler extends CommandHandler<RegisterPartyAttributeCommand, UUID> {

    private final PartyAttributesApi partyAttributesApi;

    public RegisterPartyAttributeHandler(PartyAttributesApi partyAttributesApi) {
        this.partyAttributesApi = partyAttributesApi;
    }

    @Override
    protected Mono<UUID> doHandle(RegisterPartyAttributeCommand cmd) {
        return partyAttributesApi
                .create1(cmd.getPartyId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(dto -> Objects.requireNonNull(Objects.requireNonNull(dto).getPartyAttributeId()));
    }
}
