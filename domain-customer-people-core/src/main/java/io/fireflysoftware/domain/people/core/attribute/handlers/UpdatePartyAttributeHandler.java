package io.fireflysoftware.domain.people.core.attribute.handlers;

import io.fireflysoftware.core.customer.sdk.api.PartyAttributesApi;
import io.fireflysoftware.domain.people.core.attribute.commands.UpdatePartyAttributeCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@CommandHandlerComponent
public class UpdatePartyAttributeHandler extends CommandHandler<UpdatePartyAttributeCommand, UUID> {

    private final PartyAttributesApi partyAttributesApi;

    public UpdatePartyAttributeHandler(PartyAttributesApi partyAttributesApi) {
        this.partyAttributesApi = partyAttributesApi;
    }

    @Override
    protected Mono<UUID> doHandle(UpdatePartyAttributeCommand cmd) {
        return partyAttributesApi
                .update1(cmd.getPartyId(), cmd.attributeId(), cmd, UUID.randomUUID().toString())
                .mapNotNull(dto -> Objects.requireNonNull(Objects.requireNonNull(dto).getPartyAttributeId()));
    }
}
